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

    private PriorityQueue<Link> linkQueue = new PriorityQueue<Link>();

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
        if(backlink != null){
            backlink.setSource(this);
            linkQueue.add(backlink);
        }
    }

    public ActivePriorityNode(Trie<IDocument> rootNode, QueryString queryString){
        this(rootNode, 0, EditOperation.Match, null, 0, queryString);
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
        System.out.println("List of links at " + this);
        System.out.println(linkQueue);
        Link link = GetBestLink();
        return link.UseLink();
    }

    private Link GetBestLink() {
        Link link = linkQueue.poll();
        if(link != null && link instanceof BackLink && ignoreBackLink){
            link = linkQueue.poll();
        }

        return link;
    }

    private void maybeDoFirstAdd() {
        if(!isExhausted() && !firstAdded){
            firstAdded = true;
            AddMatchToList();
            AddDeleteToList();
            AddNextEditsToList();
        }
    }

    private void AddMatchToList(){
        if(EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Match)){
            Trie<IDocument> match = queryPosition.getChildren().get(getCharacter());
            System.out.println(match);
            if(match != null){
                double rank = getDiscountRank(match, previousEdits);
                addLink(new MatchLink(rank, this, match));
            }
        }
    }

    private void AddDeleteToList(){
        System.out.println("Adding delete");
        if(EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Delete)){
            System.out.println("Adding delete2");
            int cost = EditOperation.getOperationCost(lastEditOperation, EditOperation.Delete);
            double deleteRank = getDiscountRank(queryPosition, previousEdits + cost);
            Link deleteLink = new EditLink(deleteRank, this, queryPosition, EditOperation.Delete);
            linkQueue.add(deleteLink);
        }
    }

    private void AddNextEditsToList() {
        if(EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Insert)){
            Trie<IDocument> bestEditNode = getNextEditNode();
            if(bestEditNode != null){
                double rank = getDiscountRank(bestEditNode, previousEdits + 1);
                EditLink insertLink = new EditLink(rank, this, bestEditNode, EditOperation.Insert);
                linkQueue.add(insertLink);
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
        linkQueue.add(link);
    }

    public void ignoreBackLinks(){
        ignoreBackLink = true;
    }

    public void getSuggestions(ArrayList<Trie<IDocument>> suggestionNodes, int neededSuggestions){
        int numberOfUsedSuggestions = 0;
        System.out.println("Getting suggestions, need: " + neededSuggestions);
        ArrayList<Trie<IDocument>> suggestions = queryPosition.getCachedSuggestions();
        for (int i = nextSuggestion; i < Math.min(nextSuggestion + neededSuggestions, suggestions.size()); i++){
            Trie<IDocument> suggestionDocument = suggestions.get(i);
            if(linkQueue.peek().getRank() > getDiscountRank(suggestionDocument, previousEdits)){
                break;
            }

            if(!suggestionNodes.contains(suggestionDocument)){
                suggestionNodes.add(suggestionDocument);
            }

            numberOfUsedSuggestions++;
        }

        nextSuggestion += numberOfUsedSuggestions;
        System.out.println("Got these: " + suggestionNodes);
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

        BackLink backlink = new BackLink(getNextRank(), this);
        return new ActivePriorityNode(
                position,
                previousEdits + cost,
                editOperation,
                backlink,
                queryStringIndex + movement,
                queryString);
    }

    public ForwardLink makeForwardLink(ActivePriorityNode source) {
        return new ForwardLink(getNextRank(), source ,this);
    }

    public BackLink makeBackLink(ActivePriorityNode sourceOfBackLink){
        return new BackLink(getNextRank(), sourceOfBackLink, this);
    }

    public ShortcutLink makeShortcutLink(ActivePriorityNode sourceOfShortcutLink){
        return new ShortcutLink(getNextRank(), sourceOfShortcutLink, this);
    }

    private double getNextRank(){
        double rank = -2;
        Link nextLink = linkQueue.peek();
        if(nextLink != null){
            rank = nextLink.getRank();
        }

        return rank;
        /*
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
            Link bestForwardLink = linkQueue.peek();
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
        */
    }
}
