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
    Trie<Document> queryPosition;
    int allowedEdits;
    EditOperation lastEditOperation;
    Backlink backlink;

    public ActiveQuery(
            Trie<Document> queryPosition,
            int allowedEdits,
            EditOperation lastEditOperation)
    {
        this.queryPosition = queryPosition;
        this.allowedEdits = allowedEdits;
        this.lastEditOperation = lastEditOperation;
    }

    public void addCharacter(char character, ArrayList<ActiveQuery> activeQueries){
        Map<Character, Trie<Document>> candidateQueryPositions = queryPosition.getChildren();
        for(Character candidatePath : candidateQueryPositions.keySet()){
            Trie<Document> newQueryPosition = candidateQueryPositions.get(candidatePath);
            if(candidatePath == character){
                activeQueries.add(new ActiveQuery(newQueryPosition, allowedEdits, EditOperation.Insert));
            }
            else{
                processEdits(character, newQueryPosition, activeQueries);
            }
        }

        processDeletes(activeQueries);
    }

    private void processEdits(
            char character,
            Trie<Document> newQueryPosition,
            ArrayList<ActiveQuery> activeQueries)
    {
        if(allowedEdits > 0){
            processAdd(character, newQueryPosition, activeQueries);
            processSubstitution(newQueryPosition, activeQueries);
        }
    }

    private void processAdd(
            char character,
            Trie<Document> newQueryPosition,
            ArrayList<ActiveQuery> activeQueries)
    {
        if(isAllowedToAdd()){
            ActiveQuery tempActiveQuery = new ActiveQuery(
                    newQueryPosition,
                    allowedEdits - 1,
                    EditOperation.Addition);

             tempActiveQuery.addCharacter(character, activeQueries);
        }
    }

    private boolean isAllowedToAdd() {
        return lastEditOperation != EditOperation.Substitution && lastEditOperation != EditOperation.Delete;
    }


    private void processSubstitution(Trie<Document> newQueryPosition, ArrayList<ActiveQuery> activeQueries) {
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
        return lastEditOperation != EditOperation.Addition && allowedEdits > 0;
    }

    public String toString(){
        return "ActiveQuery: " + queryPosition.getLabel() + " Last operation: " + lastEditOperation + " allowed edits: " + allowedEdits;
    }

    public String getLabel() {
        return queryPosition.getLabel();
    }
}
