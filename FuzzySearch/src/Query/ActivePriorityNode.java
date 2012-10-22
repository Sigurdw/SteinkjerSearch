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
        System.out.println(forwardLinks.size());
        queryPosition.printOrderedChildren();
        Link link = GetBestLink();
        System.out.println(link.getRank());
        return link.UseLink();
    }

    private Link GetBestLink() {
        Link link = forwardLinks.poll();
        if(link != null && link instanceof BackLink && ignoreBackLink){
            link = forwardLinks.poll();
        }

        return link;
    }

    private void maybeDoFirstAdd() {
        if(!isExhausted() && !firstAdded){
            System.out.println("initializing priority list at " + this);
            firstAdded = true;
            AddMatchToList();
            AddDeleteToList();
            AddNextEditsToList();
        }
    }

    private void AddMatchToList(){
        if(EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Insert)){
            Trie<IDocument> match = queryPosition.getChildren().get(getCharacter());
            System.out.println(match);
            if(match != null){
                double rank = getDiscountRank(match, previousEdits);
                System.out.println("Got match with label: " + match.getLabel() + " and rank: " + rank);
                addLink(new MatchLink(rank, this, match));
            }
        }
    }

    private void AddDeleteToList(){
        if(EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Delete)){
            if(lastEditOperation == EditOperation.Insert){
                double deleteRank = getDiscountRank(queryPosition, previousEdits + 1);
                Link deleteLink = new EditLink(deleteRank, this, queryPosition, EditOperation.Delete);
                forwardLinks.add(deleteLink);
            }
            else{
                Trie<IDocument> match = queryPosition.getChildren().get(getCharacter());
                if(match != null){
                    double deleteRank = getDiscountRank(match, previousEdits + 1);
                    Link deleteLink = new EditLink(deleteRank, this, queryPosition, EditOperation.Delete);
                    forwardLinks.add(deleteLink);
                }
            }
        }
    }

    private void AddNextEditsToList() {
        if(EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Insert)){
            Trie<IDocument> bestEditNode = getNextEditNode();
            if(bestEditNode != null){
                double rank = getDiscountRank(bestEditNode, previousEdits + 1);
                System.out.println("Got best with label: " + bestEditNode.getLabel() + " and rank: " + rank);
                EditLink insertLink = new EditLink(rank, this, bestEditNode, EditOperation.Insert);
                forwardLinks.add(insertLink);
            }
        }
    }

    private Trie<IDocument> getNextEditNode() {
        Trie<IDocument> bestEditNode = null;
        if(nextChild < queryPosition.getSize()){
            bestEditNode = queryPosition.getOrderedChild(nextChild);
            nextChild++;
            Trie<IDocument> match = queryPosition.getChildren().get(getCharacter());
            if(bestEditNode == match){
                System.out.println("fixing match");
                bestEditNode = null;
                if(nextChild < queryPosition.getSize()){
                    bestEditNode = queryPosition.getOrderedChild(nextChild);
                    nextChild++;
                }
            }
        }

        return bestEditNode;
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

    public ActivePriorityNode createChild(Trie<IDocument> position, EditOperation editOperation) {
        int cost = EditOperation.getOperationCost(lastEditOperation, editOperation);
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
