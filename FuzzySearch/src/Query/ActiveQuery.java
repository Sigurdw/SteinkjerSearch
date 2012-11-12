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

    private final int MaxEdits = 3;

    public ActiveQuery(
            Trie<IDocument> queryPosition,
            int previousEdtis,
            EditOperation lastEditOperation,
            QueryString queryString,
            int queryStringIndex,
            boolean isSubstitution)
    {
        this.queryPosition = queryPosition;
        this.previousEdits = previousEdtis;
        this.lastEditOperation = lastEditOperation;
        this.queryString = queryString;
        this.queryStringIndex = queryStringIndex;
        substitution = isSubstitution;
    }

    public void addCharacter(ArrayList<ActiveQuery> activeQueries){
        Map<Character, Trie<IDocument>> candidateQueryPositions = queryPosition.getChildren();
        for(Character candidatePath : candidateQueryPositions.keySet()){
            Trie<IDocument> newQueryPosition = candidateQueryPositions.get(candidatePath);
            if(candidatePath == queryString.GetCharacter(queryStringIndex)){
                activeQueries.add(new ActiveQuery(
                        newQueryPosition,
                        previousEdits,
                        EditOperation.Match,
                        queryString,
                        queryStringIndex + 1,
                        false));
            }
            else{
                processEdits(newQueryPosition, activeQueries);
            }
        }

        processDeletes(activeQueries);
    }

    private void processEdits(
            Trie<IDocument> newQueryPosition,
            ArrayList<ActiveQuery> activeQueries)
    {
        if(previousEdits > 0){
            processInsert(newQueryPosition, activeQueries);
        }
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
                    false);

             tempActiveQuery.addCharacter(activeQueries);
        }
    }

    private boolean isAllowedToInsert() {
        return (lastEditOperation != EditOperation.Delete || substitution) && previousEdits < MaxEdits;
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
                    queryStringIndex,
                    isSubstitution));
        }
    }

    private boolean isAllowToDelete() {
        return lastEditOperation == EditOperation.Insert || previousEdits < MaxEdits;
    }

    public String toString(){
        return "Query.ActiveQuery: " + queryPosition.getLabel() + " Last operation: " + lastEditOperation + " allowed edits: " + previousEdits;
    }

    public String getLabel() {
        return queryPosition.getLabel();
    }

    public void getSuggestions(ArrayList<ISuggestionWrapper> suggestions) {
        ArrayList<Trie<IDocument>> cachedSuggestions = queryPosition.getCachedSuggestions();
        for (Trie<IDocument> suggestion : cachedSuggestions){
            boolean hasSuggestion = false;
            for(ISuggestionWrapper suggestionWrapper : suggestions){
                if(suggestionWrapper.getSuggestion().equals(suggestion.getLabel())){
                    hasSuggestion = true;
                    break;
                }
            }

            if(!hasSuggestion){
                suggestions.add(new SuggestionWrapper(suggestion.getLabel(), suggestion.getRank() * EditOperation.getRankDiscount(previousEdits)));
            }
        }
    }
}
