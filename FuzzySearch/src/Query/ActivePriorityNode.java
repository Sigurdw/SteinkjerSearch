package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;

import java.util.ArrayList;
import java.util.List;
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
    private double editDiscount;
    private boolean isSubstitution;
    private ILinkDiscarder linkDiscarder;

    private PriorityQueue<Link> linkQueue = new PriorityQueue<Link>();
    private boolean perserveLink = false;

    private ActivePriorityNode(
            Trie<IDocument> queryPosition,
            int previousEdits,
            EditOperation lastEditOperation,
            BackLink backlink,
            int queryStringIndex,
            QueryString queryString,
            double editDiscount,
            boolean isSubstitution,
            ILinkDiscarder linkDiscarder
    )
    {
        this.queryPosition = queryPosition;
        this.previousEdits = previousEdits;
        this.lastEditOperation = lastEditOperation;
        this.queryStringIndex = queryStringIndex;
        this.queryString = queryString;
        this.editDiscount = editDiscount;
        this.isSubstitution = isSubstitution;
        this.linkDiscarder = linkDiscarder;

        if(backlink != null){
            backlink.setSource(this);
            linkQueue.add(backlink);
        }
    }

    public ActivePriorityNode(
            Trie<IDocument> rootNode,
            QueryString queryString,
            ILinkDiscarder linkDiscarder)
    {
        this(rootNode, 0, EditOperation.Match, null, 0, queryString, 1, false, linkDiscarder);
    }

    public String toString(){
        return "Query.ActiveQuery: " + queryPosition.getLabel() + " Last operation: " + lastEditOperation + " previous edits: " + previousEdits + " rank: " + getRank();
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
        if(link.getRank() <= 0){
            return null;
        }

        return link.UseLink();
    }

    private Link GetBestLink() {
        Link link = linkQueue.poll();
        link = maybeSwitch(link);

        while(link != null && ((link instanceof BackLink && ignoreBackLink) || !link.isValid(queryString))){
            link = linkQueue.poll();
            link = maybeSwitch(link);
        }

        return link;
    }

    private Link maybeSwitch(Link link){
        Link secondLink = linkQueue.peek();
        if(link != null && secondLink != null){
            if(
                    link instanceof BackLink &&
                    link.getRank() == secondLink.getRank()&&
                    !(secondLink instanceof ShortcutLink)){
                linkQueue.poll();
                linkQueue.add(link);
                link = secondLink;
            }
            else if( link.getRank() == secondLink.getRank() && secondLink instanceof MatchLink){
                linkQueue.poll();
                linkQueue.add(link);
                link = secondLink;
            }
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
            Trie<IDocument> match = getMatch();
            System.out.println(match);
            if(match != null){
                double rank = getDiscountRank(match, EditOperation.getOperationDiscount(
                        EditOperation.Match,
                        previousEdits));
                int movement = EditOperation.getOperationMovement(EditOperation.Match);
                addLink(new MatchLink(
                            rank,
                            this,
                            match,
                            previousEdits,
                            queryStringIndex + movement,
                            editDiscount));
            }
        }
    }

    private void AddDeleteToList(){
        if(canDelete()){
            int cost = EditOperation.getOperationCost(lastEditOperation, EditOperation.Delete);
            double modifier = 1;
            if(cost != 0){
                modifier = EditOperation.getOperationDiscount(EditOperation.Delete, previousEdits);
            }

            int movement = EditOperation.getOperationMovement(EditOperation.Delete);

            double deleteRank = getDiscountRank(
                    queryPosition,
                    modifier);
            EditLink deleteLink = new EditLink(
                    deleteRank,
                    this,
                    queryPosition,
                    EditOperation.Delete,
                    previousEdits + cost,
                    queryStringIndex + movement,
                    editDiscount * modifier,
                    cost == 0);

            if(deleteLink.isValid(queryString)){
                linkQueue.add(deleteLink);
            }
            else{
                linkDiscarder.discardLink(deleteLink);
            }
        }
    }

    private boolean canDelete(){
        return EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Delete)
                    && (!(getMatch() != null && queryPosition.getSize() == 1)
                        || lastEditOperation == EditOperation.Insert);
    }

    private Trie<IDocument> getMatch(){
        return queryPosition.getChildren().get(getCharacter());
    }

    private void AddNextEditsToList() {
        EditLink insertLink = GetNextEditLink();
        if(insertLink != null){
            if(insertLink.isValid(queryString)){
                linkQueue.add(insertLink);
            }
            else{
                linkDiscarder.discardLink(insertLink);
            }
        }
    }

    public EditLink stealNextEditLink(){
        return GetNextEditLink();
    }

    private EditLink GetNextEditLink(){
        if(EditOperation.isOperationAllowed(lastEditOperation, EditOperation.Insert) || isSubstitution){
            Trie<IDocument> bestEditNode = getNextEditNode();
            if(bestEditNode != null){
                double modifier = EditOperation.getOperationDiscount(EditOperation.Insert, previousEdits);
                double rank = getDiscountRank(
                        bestEditNode,
                        modifier);

                int cost = EditOperation.getOperationCost(lastEditOperation, EditOperation.Insert);
                int movement = EditOperation.getOperationMovement(EditOperation.Insert);

                EditLink insertLink = new EditLink(
                        rank,
                        this,
                        bestEditNode,
                        EditOperation.Insert,
                        previousEdits + cost,
                        queryStringIndex + movement,
                        editDiscount * modifier,
                        false);

                return insertLink;
            }
        }

        return null;
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
        ArrayList<Trie<IDocument>> suggestions = queryPosition.getCachedSuggestions();
        for (int i = nextSuggestion; i < Math.min(nextSuggestion + neededSuggestions, suggestions.size()); i++){
            Trie<IDocument> suggestionDocument = suggestions.get(i);
            double suggestionRank = getDiscountRank(suggestionDocument, 1);
            if(linkQueue.peek().getRank() > suggestionRank){
                break;
            }

            if(!suggestionNodes.contains(suggestionDocument)){
                suggestionNodes.add(suggestionDocument);
                numberOfUsedSuggestions++;
            }
            else{
                nextSuggestion++;
            }
        }

        nextSuggestion += numberOfUsedSuggestions;
        System.out.println("Got these: " + suggestionNodes);
    }

    public double getRank() {
        return getDiscountRank(queryPosition, 1);
    }

    private double getDiscountRank(Trie<IDocument> node, double modifier){
        return node.getRank() * modifier * editDiscount;
    }

    public void maybyAddNextActiveNode(EditOperation editOperation){
        if(editOperation == EditOperation.Insert){
            AddNextEditsToList();
        }
    }

    public ActivePriorityNode createChild(
            Trie<IDocument> position,
            EditOperation editOperation,
            int numberOfEdits,
            int queryStringIndex,
            double editDiscount,
            boolean isSubstitution)
    {
        if(editOperation == EditOperation.Insert){
            AddNextEditsToList();
        }

        BackLink backlink = new BackLink(getNextRank(), this);

        return new ActivePriorityNode(
                position,
                numberOfEdits,
                editOperation,
                backlink,
                queryStringIndex,
                queryString,
                editDiscount,
                isSubstitution,
                linkDiscarder
                );
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

    public Link makeSuggestionLink(ActivePriorityNode sourceOfSuggestionLink){
        Link forwardLink;
        if(perserveLink){
            forwardLink = new ForwardLink(getRank(), sourceOfSuggestionLink, this);
        }
        else{
            forwardLink = new SuggestionLink(
                    getNextSuggestionRank(),
                    queryString.GetLength(),
                    sourceOfSuggestionLink,
                    this);
        }

        return forwardLink;
    }

    private double getNextSuggestionRank() {
        if(nextSuggestion < queryPosition.getCachedSuggestions().size()){
            return getDiscountRank(queryPosition.getCachedSuggestions().get(nextSuggestion), 1);
        }
        else{
            return -2;
        }
    }

    private double getNextRank(){
        double rank = -2;
        Link nextLink = linkQueue.peek();
        if(nextLink != null){
            rank = nextLink.getRank();
        }

        return rank;
    }

    public EditOperation getLastOperation() {
        return lastEditOperation;
    }

    public void perserveLink(){
        perserveLink = true;
    }

    public BackLink stealBacklink() {
        Link backlink = linkQueue.poll();
        System.out.println(backlink);
        return (BackLink)backlink;
    }
}
