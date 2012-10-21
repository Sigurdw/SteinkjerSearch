package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class ActivePriorityNode {
    private Trie<IDocument> queryPosition;
    private int previousEdits;
    private int queryStringIndex;
    private EditOperation lastEditOperation;
    private int nextChild = 0;
    private boolean firstAdded = false;
    private boolean ignoreBackLink = false;
    private int nextSuggestion = 0;
    private QueryString queryString;

    private PriorityQueue<Link> forwardLinks = new PriorityQueue<Link>();

    ActivePriorityNode(
            Trie<IDocument> queryPosition,
            int previousEdits,
            EditOperation lastEditOperation,
            BackLink backlink,
            int queryStringIndex,
            QueryString queryString
    )
    {
        this.queryPosition = queryPosition;
        this.previousEdits = previousEdits;
        this.lastEditOperation = lastEditOperation;
        this.queryStringIndex = queryStringIndex;
        this.queryString = queryString;
        forwardLinks.add(backlink);
    }

    public ActivePriorityNode(Trie<IDocument> rootNode, QueryString queryString){
        this(rootNode, 0, EditOperation.Match, new BackLink(-1, null), 0, queryString);
    }

    public String toString(){
        return "Query.ActiveQuery: " + queryPosition.getLabel() + " Last operation: " + lastEditOperation + " previous edits: " + previousEdits + " rank; " + queryPosition.getRank();
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
        maybeDoFirstAdd();
        System.out.println("Getting best next node at " + this + " with children:");
        queryPosition.printOrderedChildren();
        Link link = GetBestLink();
        return link.UseLink();
    }

    private void maybeDoFirstAdd() {
        if(!firstAdded){
            firstAdded = true;
            AddMatchToList();
            AddNextEditsToList();
        }
    }

    private Link GetBestLink() {
        Link link = forwardLinks.poll();
        if(link != null && link instanceof BackLink && ignoreBackLink){
            link = forwardLinks.poll();
        }

        return link;
    }

    private void AddMatchToList(){
        Trie<IDocument> match = queryPosition.getChildren().get(getCharacter());
        if(match != null && !firstAdded){
            double rank = getDiscountRank(match, previousEdits);
            System.out.println("Got match with label: " + match.getLabel() + " and rank: " + rank);
            addLink(new MatchLink(rank, this, match));
        }
    }

    private void AddNextEditsToList() {
        Trie<IDocument> bestEditNode = getNextEditNode();
        if(bestEditNode != null){
            double rank = getDiscountRank(bestEditNode, previousEdits + 1);
            System.out.println("Got best with label: " + bestEditNode.getLabel() + " and rank: " + rank);
            EditLink substitutionLink = new EditLink(rank, this, bestEditNode, EditOperation.Substitution);
            forwardLinks.add(substitutionLink);
            EditLink insertLink = new EditLink(rank, this, bestEditNode, EditOperation.Insert);
            forwardLinks.add(insertLink);
        }
    }

    private Trie<IDocument> getNextEditNode() {
        Trie<IDocument> bestEditNode = null;
        if(nextChild < queryPosition.getSize()){
            bestEditNode = queryPosition.getOrderedChild(nextChild);
            Trie<IDocument> match = queryPosition.getChildren().get(getCharacter());
            if(bestEditNode == match){
                System.out.println("fixing match");
                nextChild++;
                bestEditNode = null;
                if(nextChild < queryPosition.getSize()){
                    bestEditNode = queryPosition.getOrderedChild(nextChild);
                }
            }
        }

        return bestEditNode;
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

        if(link instanceof ShortcutLink){
            ignoreBackLink = true;
        }
    }

    public void getSuggestions(ArrayList<String> suggestionList, int neededSuggestions){
        int numberOfUsedSuggestions = 0;
        ArrayList<Trie<IDocument>> suggestions = queryPosition.getCachedSuggestions();
        for (int i = nextSuggestion; i < Math.max(nextSuggestion + neededSuggestions, suggestions.size()); i++){
            Trie<IDocument> suggestionDocument = suggestions.get(i);
            if(forwardLinks.peek().getRank() > getDiscountRank(suggestionDocument, previousEdits)){
                System.out.println("Suggestions: " + suggestions);
                System.out.println("Node: " + suggestionDocument + ", rank: " + getDiscountRank(suggestionDocument, previousEdits));
                System.out.println("previous edits: " + previousEdits);
                System.out.println("Done finding suggestions here. " + i + ", " + nextSuggestion);
                break;
            }

            suggestionList.add(suggestionDocument.getLabel() + ", " + getDiscountRank(suggestionDocument, previousEdits));
            numberOfUsedSuggestions++;
        }

        nextSuggestion += numberOfUsedSuggestions;

        System.out.println("Got these:");
        System.out.println(suggestionList);
    }

    private double getNextRank(){
        if(!isExhausted()){
            double bestEditNodeRank = -1;
            Trie<IDocument> bestNode = null;
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

    private static double getDiscountRank(Trie<IDocument> node, int edits){
        return node.getRank() * Math.pow(0.5, edits);
    }

    private void printRanks(double[] ranks){
        for(double rank : ranks){
            System.out.println("Rank: " + rank);
        }
    }

    public ActivePriorityNode createChild(Trie<IDocument> position, EditOperation editOperation) {
        int cost = EditOperation.getOperationCost(editOperation);
        int movement = EditOperation.getOperationMovement(editOperation);
        if(editOperation == EditOperation.Insert){
            AddNextEditsToList();
        }

        BackLink backlink = new BackLink(forwardLinks.peek().getRank(), this);
        return new ActivePriorityNode(
                position,
                previousEdits + cost,
                editOperation,
                backlink,
                queryStringIndex + movement,
                queryString);
    }
}
