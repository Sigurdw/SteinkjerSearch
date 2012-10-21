package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        28.09.12
 * Time:        16:38
 * To change this template use File | Settings | File Templates.
 */
public class ActiveQuery {
    Trie<IDocument> queryPosition;
    int allowedEdits;
    EditOperation lastEditOperation;
    Link backlink;

    public ActiveQuery(
            Trie<IDocument> queryPosition,
            int allowedEdits,
            EditOperation lastEditOperation)
    {
        this.queryPosition = queryPosition;
        this.allowedEdits = allowedEdits;
        this.lastEditOperation = lastEditOperation;
    }

    public void addCharacter(char character, ArrayList<ActiveQuery> activeQueries){
        Map<Character, Trie<IDocument>> candidateQueryPositions = queryPosition.getChildren();
        for(Character candidatePath : candidateQueryPositions.keySet()){
            Trie<IDocument> newQueryPosition = candidateQueryPositions.get(candidatePath);
            if(candidatePath == character){
                activeQueries.add(new ActiveQuery(newQueryPosition, allowedEdits, EditOperation.Match));
            }
            else{
                processEdits(character, newQueryPosition, activeQueries);
            }
        }

        processDeletes(activeQueries);
    }

    private void processEdits(
            char character,
            Trie<IDocument> newQueryPosition,
            ArrayList<ActiveQuery> activeQueries)
    {
        if(allowedEdits > 0){
            processAdd(character, newQueryPosition, activeQueries);
            processSubstitution(newQueryPosition, activeQueries);
        }
    }

    private void processAdd(
            char character,
            Trie<IDocument> newQueryPosition,
            ArrayList<ActiveQuery> activeQueries)
    {
        if(isAllowedToAdd()){
            ActiveQuery tempActiveQuery = new ActiveQuery(
                    newQueryPosition,
                    allowedEdits - 1,
                    EditOperation.Insert);

             tempActiveQuery.addCharacter(character, activeQueries);
        }
    }

    private boolean isAllowedToAdd() {
        return lastEditOperation != EditOperation.Substitution && lastEditOperation != EditOperation.Delete;
    }


    private void processSubstitution(Trie<IDocument> newQueryPosition, ArrayList<ActiveQuery> activeQueries) {
        if(isAllowedToSubstitute()){
            activeQueries.add(
                    new ActiveQuery(newQueryPosition, allowedEdits - 1, EditOperation.Substitution));
        }
    }

    private boolean isAllowedToSubstitute() {
        return lastEditOperation != EditOperation.Delete;
    }

    private void processDeletes(ArrayList<ActiveQuery> activeQueries) {
        if(isAllowToDelete()){
            activeQueries.add(new ActiveQuery(queryPosition, allowedEdits - 1, EditOperation.Delete));
        }
    }

    private boolean isAllowToDelete() {
        return lastEditOperation != EditOperation.Insert && allowedEdits > 0;
    }

    public String toString(){
        return "Query.ActiveQuery: " + queryPosition.getLabel() + " Last operation: " + lastEditOperation + " allowed edits: " + allowedEdits;
    }

    public String getLabel() {
        return queryPosition.getLabel();
    }
}
