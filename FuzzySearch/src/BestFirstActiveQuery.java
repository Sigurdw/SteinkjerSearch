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
public class BestFirstActiveQuery {
    private Trie<Document> queryPosition;
    private int previousEdits;
    private char character = 0;
    private EditOperation lastEditOperation;
    private int nextChild = 0;
    private Backlink backlink;
    private boolean matchUsed = false;
    private int nextSuggestion = 0;

    private PriorityQueue<Backlink> forwardLinks = new PriorityQueue<Backlink>();

    private BestFirstActiveQuery(
            Trie<Document> queryPosition,
            int previousEdits,
            EditOperation lastEditOperation,
            Backlink backlink
            )
    {
        this.queryPosition = queryPosition;
        this.previousEdits = previousEdits;
        this.lastEditOperation = lastEditOperation;
        this.backlink = backlink;
    }

    public BestFirstActiveQuery(Trie<Document> rootNode){
        this(rootNode, 0, EditOperation.Insert, new Backlink(-1, null));
    }

    public String toString(){
        return "ActiveQuery: " + queryPosition.getLabel() + " Last operation: " + lastEditOperation + " previous edits: " + previousEdits + " rank; " + queryPosition.getRank();
    }

    public String getLabel() {
        return queryPosition.getLabel();
    }

    public boolean isExhausted() {
        return character == 0;
    }

    public BestFirstActiveQuery getBestNextActiveNode(char character) {
        System.out.println("Getting the best node.");
        this.character = character;
        Trie<Document> match = queryPosition.getChildren().get(character);
        double[] ranks = {0, 0, backlink.getRank(), 0};
        if(match != null && !matchUsed){
            ranks[0] = match.getRank();
            System.out.println("Got match with label: " + match.getLabel() + " and rank: " + match.getRank());
        }

        Trie<Document> bestNode = null;
        if(nextChild < queryPosition.getSize()){
            bestNode = queryPosition.getOrderedChild(nextChild);
        }

        if(bestNode != null){
            System.out.println("Got best with label: " + bestNode.getLabel() + " and rank: " + bestNode.getRank());
            if(bestNode == match){
                ranks[1] = -1;
                nextChild++;
            }
            else{
                ranks[1] = bestNode.getRank() * 0.5;
            }
        }

        Backlink bestForwardLink = forwardLinks.peek();
        if(bestForwardLink != null){
            ranks[3] = bestForwardLink.getRank();
            System.out.println("Got best forward link with label: " + bestForwardLink.getActiveQuery().getLabel() + " and rank: " + bestForwardLink.getRank());
        }

        BestFirstActiveQuery nextActiveQuery = null;
        Backlink newBacklink;

        switch (argMax(ranks)){
            case 0:
                matchUsed = true;
                newBacklink = new Backlink(Math.max(Math.max(ranks[1], ranks[2]), ranks[3]), this);
                nextActiveQuery = new BestFirstActiveQuery(match, previousEdits, EditOperation.Insert, newBacklink);
                break;
            case 1:
                newBacklink = new Backlink(Math.max(Math.max(ranks[0], ranks[2]), ranks[3]), this);
                nextActiveQuery = new BestFirstActiveQuery(bestNode, previousEdits + 1, EditOperation.Substitution, newBacklink);
                nextChild++;
                break;
            case 2:
                newBacklink = new Backlink(Math.max(ranks[1], ranks[3]), this);
                nextActiveQuery = backlink.getActiveQuery();
                nextActiveQuery.addLink(newBacklink);
                break;
            case 3:
                newBacklink = new Backlink(Math.max(Math.max(ranks[0], ranks[1]), ranks[2]), this);
                nextActiveQuery = forwardLinks.poll().getActiveQuery();
                System.out.println("Traveling the a forward link: " + nextActiveQuery);
                nextActiveQuery.setBacklink(newBacklink);
                break;
        }

        return nextActiveQuery;
    }

    private void setBacklink(Backlink newBacklink) {
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

    public void addLink(Backlink backlink){
        forwardLinks.add(backlink);
    }

    public void getSuggestions(ArrayList<String> suggestionList){
        for(int i = nextSuggestion; i < queryPosition.getSize(); i++){
            Trie<Document> suggestionDocument = queryPosition.getOrderedChild(i);
            if(backlink.getRank() > suggestionDocument.getRank()){
                System.out.println("Done finding suggestions here.");
                nextSuggestion = i;
                break;
            }


            suggestionList.add(suggestionDocument.toString());
        }

        System.out.println("Got these:");
        System.out.println(suggestionList);
    }

    public BestFirstActiveQuery travelTheBacklink() {
        System.out.println("Traveling the backlink.");
        BestFirstActiveQuery backnode = backlink.getActiveQuery();
        //todo get the actual next rank:
        backnode.addLink(new Backlink(getNextRank(), this));
        return backnode;
    }

    private double getNextRank(){
        if(!isExhausted()){
            double bestEditNodeRank = -1;
            Trie<Document> bestNode = null;
            if(nextChild < queryPosition.getSize()){
                bestNode = queryPosition.getOrderedChild(nextChild);
            }

            if(bestNode != null && bestNode == queryPosition.getChildren().get(character)){
                bestNode = null;
                nextChild++;
                if(nextChild < queryPosition.getSize()){
                    bestNode = queryPosition.getOrderedChild(nextChild);
                }
            }

            if(bestNode != null){
                bestEditNodeRank = bestNode.getRank() * 0.5;
            }

            double bestForwardNodeRank = -1;
            Backlink bestForwardLink = forwardLinks.peek();
            if(bestForwardLink != null){
                bestForwardNodeRank = bestForwardLink.getRank();
            }

            return Math.max(bestEditNodeRank, bestForwardNodeRank);
        }
        else{
            if(nextSuggestion < queryPosition.getSize()){
                return queryPosition.getOrderedChild(nextSuggestion).getRank();
            }
            else{
                return -1;
            }
        }

    }

    public double getRank() {
        return queryPosition.getRank() * previousEdits * 0.5;
    }
}
