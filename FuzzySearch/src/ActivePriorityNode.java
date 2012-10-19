import java.util.ArrayList;
import java.util.PriorityQueue;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        10.10.12
 * Time:        12:31
 * To change this template use File | Settings | File Templates.
 */
public class ActivePriorityNode {
    private Trie<Document> queryPosition;
    private int previousEdits;
    private int queryStringIndex;
    private EditOperation lastEditOperation;
    private int nextChild = 0;
    private Link backlink;
    private boolean matchUsed = false;
    private int nextSuggestion = 0;
    private QueryString queryString;

    private PriorityQueue<Link> forwardLinks = new PriorityQueue<Link>();

    private ActivePriorityNode(
            Trie<Document> queryPosition,
            int previousEdits,
            EditOperation lastEditOperation,
            Link backlink,
            int queryStringIndex,
            QueryString queryString
    )
    {
        this.queryPosition = queryPosition;
        this.previousEdits = previousEdits;
        this.lastEditOperation = lastEditOperation;
        this.backlink = backlink;
        this.queryStringIndex = queryStringIndex;
        this.queryString = queryString;
    }

    public ActivePriorityNode(Trie<Document> rootNode, QueryString queryString){
        this(rootNode, 0, EditOperation.Insert, new Link(-1, null), 0, queryString);
    }

    public String toString(){
        return "ActiveQuery: " + queryPosition.getLabel() + " Last operation: " + lastEditOperation + " previous edits: " + previousEdits + " rank; " + queryPosition.getRank();
    }

    public String getLabel() {
        return queryPosition.getLabel();
    }

    private char getCharacter(){
        return queryString.GetCharacter(queryStringIndex);
    }

    public boolean isExhausted() {
        return queryString.IsExhausted(queryStringIndex);
    }

    public ActivePriorityNode getBestNextActiveNode() {
        System.out.println("Getting best next node at " + this + " with children:");
        queryPosition.printOrderedChildren();
        double[] ranks = {0, 0, backlink.getRank(), 0};

        Trie<Document> match = queryPosition.getChildren().get(getCharacter());
        if(match != null && !matchUsed){
            ranks[0] = getDiscountRank(match, previousEdits);
            System.out.println("Got match with label: " + match.getLabel() + " and rank: " + match.getRank());
        }

        Trie<Document> bestNode = null;
        if(nextChild < queryPosition.getSize()){
            bestNode = queryPosition.getOrderedChild(nextChild);
        }

        if(bestNode != null){
            if(bestNode == match){
                nextChild++;
                if(nextChild < queryPosition.getSize()){
                    bestNode = queryPosition.getOrderedChild(nextChild);
                }
            }

            if(bestNode != null) {
                ranks[1] = getDiscountRank(bestNode, previousEdits + 1);
                System.out.println("Got best with label: " + bestNode.getLabel() + " and rank: " + ranks[1]);
            }
        }

        Link bestForwardLink = forwardLinks.peek();
        if(bestForwardLink != null){
            ranks[3] = bestForwardLink.getRank();
            System.out.println("Got best forward link with label: " + bestForwardLink.getActivePriorityNode().getLabel() + " and rank: " + bestForwardLink.getRank());
        }

        ActivePriorityNode nextActivePriorityNode = null;
        Link newBacklink;

        System.out.println("Choosing among these ranks:");
        printRanks(ranks);

        switch (argMax(ranks)){
            case 0:
                matchUsed = true;
                newBacklink = new Link(Math.max(Math.max(ranks[1], ranks[2]), ranks[3]), this);
                nextActivePriorityNode = new ActivePriorityNode(match, previousEdits, EditOperation.Insert, newBacklink, queryStringIndex + 1, queryString);
                System.out.println("Match: " + nextActivePriorityNode);
                break;
            case 1:
                newBacklink = new Link(Math.max(Math.max(ranks[0], ranks[2]), ranks[3]), this);
                nextActivePriorityNode = new ActivePriorityNode(bestNode, previousEdits + 1, EditOperation.Substitution, newBacklink, queryStringIndex + 1, queryString);
                System.out.println("Doing a substitution: " + nextActivePriorityNode);
                nextChild++;
                break;
            case 2:
                newBacklink = new Link(Math.max(ranks[1], ranks[3]), this);
                nextActivePriorityNode = backlink.getActivePriorityNode();
                nextActivePriorityNode.addLink(newBacklink);
                System.out.println("Traveling the a backlink: " + nextActivePriorityNode);
                break;
            case 3:
                newBacklink = new Link(Math.max(Math.max(ranks[0], ranks[1]), ranks[2]), this);
                Link forwardLink = forwardLinks.poll();
                nextActivePriorityNode = forwardLink.getActivePriorityNode();

                System.out.println("Traveling the forward link: " + nextActivePriorityNode);
                if(forwardLink.isShortCut()){
                    nextActivePriorityNode.addLink(newBacklink);
                }
                else{
                    nextActivePriorityNode.setBacklink(newBacklink);
                }

                break;
        }

        return nextActivePriorityNode;
    }

    private void setBacklink(Link newBacklink) {
        backlink = newBacklink;
    }

    private int argMax(double[] list){
        int maxIndex = 0;
        for(int i = 1; i < list.length; i++){
            if(list[i] > list [maxIndex]){
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    public void addLink(Link link){
        forwardLinks.add(link);

        if(link.isShortCut()){
            backlink = new Link(-1, null);
        }
    }

    public void getSuggestions(ArrayList<String> suggestionList){
        int numberOfUsedSuggestions = 0;
        for(int i = nextSuggestion; i < queryPosition.getSize(); i++){
            Trie<Document> suggestionDocument = queryPosition.getOrderedChild(i);
            if(backlink.getRank() > getDiscountRank(suggestionDocument, previousEdits)){
                System.out.println("Done finding suggestions here. " + i + ", " + nextSuggestion);
                break;
            }

            suggestionList.add(suggestionDocument.getLabel() + ", " + getDiscountRank(suggestionDocument, previousEdits));
            numberOfUsedSuggestions++;
        }

        nextSuggestion += numberOfUsedSuggestions;

        System.out.println("Got these:");
        System.out.println(suggestionList);
        if(nextSuggestion < queryPosition.getSize()){
            System.out.println("Next suggestion is: " + nextSuggestion + ", point to: " + queryPosition.getOrderedChild(nextSuggestion));
        }
    }

    public ActivePriorityNode travelTheBacklink() {
        System.out.println("Traveling the backlink.");
        ActivePriorityNode backnode = backlink.getActivePriorityNode();
        //todo get the actual next rank:
        backnode.addLink(new Link(getNextRank(), this));
        return backnode;
    }

    private double getNextRank(){
        if(!isExhausted()){
            double bestEditNodeRank = -1;
            Trie<Document> bestNode = null;
            if(nextChild < queryPosition.getSize()){
                bestNode = queryPosition.getOrderedChild(nextChild);
            }

            if(bestNode != null && bestNode == queryPosition.getChildren().get(getCharacter())){
                bestNode = null;
                nextChild++;
                if(nextChild < queryPosition.getSize()){
                    bestNode = queryPosition.getOrderedChild(nextChild);
                }
            }

            if(bestNode != null){
                bestEditNodeRank = getDiscountRank(bestNode, previousEdits + 1);
            }

            double bestForwardNodeRank = -1;
            Link bestForwardLink = forwardLinks.peek();
            if(bestForwardLink != null){
                bestForwardNodeRank = bestForwardLink.getRank();
            }

            return Math.max(bestEditNodeRank, bestForwardNodeRank);
        }
        else{
            if(nextSuggestion < queryPosition.getSize()){
                return getDiscountRank(queryPosition.getOrderedChild(nextSuggestion), previousEdits);
            }
            else{
                return -1;
            }
        }

    }

    public double getRank() {
        return getDiscountRank(queryPosition, previousEdits);
    }

    private static double getDiscountRank(Trie<Document> node, int edits){
        return node.getRank() * Math.pow(0.5, edits);
    }

    private void printRanks(double[] ranks){
        for(double rank : ranks){
            System.out.println("Rank: " + rank);
        }
    }
}
