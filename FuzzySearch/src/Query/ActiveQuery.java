package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;

import java.util.*;

public class ActiveQuery {
    Trie<IDocument> queryPosition;
    int previousEdits;
    EditOperation lastEditOperation;
    private QueryString queryString;
    private int queryStringIndex;
    private boolean substitution;

    private final int allowedEditDistance;

    public ActiveQuery(Trie<IDocument> queryPosition, QueryString queryString, int allowedEditDistance){
        this(queryPosition, 0, EditOperation.Match, queryString, 0, false, allowedEditDistance);
    }

    private ActiveQuery(
            Trie<IDocument> queryPosition,
            int previousEdtis,
            EditOperation lastEditOperation,
            QueryString queryString,
            int queryStringIndex,
            boolean isSubstitution,
            int allowedEditDistance)
    {
        this.queryPosition = queryPosition;
        this.previousEdits = previousEdtis;
        this.lastEditOperation = lastEditOperation;
        this.queryString = queryString;
        this.queryStringIndex = queryStringIndex;
        this.allowedEditDistance = allowedEditDistance;
        substitution = isSubstitution;
    }

    public void addCharacter(ArrayList<ActiveQuery> activeQueries){
        Map<Character, Trie<IDocument>> candidateQueryPositions = queryPosition.getChildren();

        if(getLabel().equals("pr")){
            System.out.println("VERBOSE: " + this + queryString.GetCharacter(queryStringIndex) + ", " + queryString.GetLastCharacter());
        }

        for(Character candidatePath : candidateQueryPositions.keySet()){
            Trie<IDocument> newQueryPosition = candidateQueryPositions.get(candidatePath);
            if(candidatePath == queryString.GetCharacter(queryStringIndex)){
                activeQueries.add(new ActiveQuery(
                        newQueryPosition,
                        previousEdits,
                        EditOperation.Match,
                        queryString,
                        queryStringIndex + 1,
                        false,
                        allowedEditDistance));
            }
            else{
                processInsert(newQueryPosition, activeQueries);
            }
        }

        processDeletes(activeQueries);
    }

    private void processInsert(Trie<IDocument> newQueryPosition, ArrayList<ActiveQuery> activeQueries)
    {
        if(isAllowedToInsert()){
            ActiveQuery tempActiveQuery = new ActiveQuery(
                    newQueryPosition,
                    previousEdits + 1,
                    EditOperation.Insert,
                    queryString,
                    queryStringIndex,
                    false,
                    allowedEditDistance);

             tempActiveQuery.addCharacter(activeQueries);
        }
    }

    private boolean isAllowedToInsert() {
        return (lastEditOperation != EditOperation.Delete || substitution) && previousEdits < allowedEditDistance;
    }

    private void processDeletes(ArrayList<ActiveQuery> activeQueries) {
        if(isAllowToDelete()){
            int editCost = EditOperation.getOperationCost(lastEditOperation, EditOperation.Delete);
            boolean isSubstitution  = false;
            if(editCost == 0){
                isSubstitution = true;
            }

            activeQueries.add(new ActiveQuery(
                    queryPosition,
                    previousEdits + editCost,
                    EditOperation.Delete,
                    queryString,
                    queryStringIndex + 1,
                    isSubstitution,
                    allowedEditDistance));
        }
    }

    private boolean isAllowToDelete() {
        return lastEditOperation == EditOperation.Insert || previousEdits < allowedEditDistance;
    }

    public String toString(){
        return  queryPosition.getLabel() + "Rank: " + queryPosition.getRank() * EditOperation.getRankDiscount(previousEdits) +  ", Last operation: " + lastEditOperation + " edits done: " + previousEdits;
    }

    public String getLabel() {
        return queryPosition.getLabel();
    }

    public void getSuggestions(ArrayList<ISuggestionWrapper> suggestions) {
        ArrayList<Trie<IDocument>> cachedSuggestions = queryPosition.getCachedSuggestions();
        for (Trie<IDocument> suggestion : cachedSuggestions){
            double suggestionRank = suggestion.getRank() * EditOperation.getRankDiscount(previousEdits);
            boolean hasSuggestion = false;
            for(int i = 0; i < suggestions.size(); i++){
                ISuggestionWrapper suggestionWrapper = suggestions.get(i);
                if(suggestionWrapper.getSuggestion().equals(suggestion.getLabel())){
                    if(suggestionWrapper.getRank() < suggestionRank){
                        suggestions.set(i, new SuggestionWrapper(suggestion.getLabel(), suggestionRank));
                    }
                    hasSuggestion = true;
                    break;
                }
            }

            if(!hasSuggestion){
                suggestions.add(new SuggestionWrapper(suggestion.getLabel(), suggestionRank));
            }
        }
    }
}
