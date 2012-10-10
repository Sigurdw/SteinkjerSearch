import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        10.10.12
 * Time:        16:07
 * To change this template use File | Settings | File Templates.
 */
public class TriePriorityTraverser {
    private ArrayList<BestFirstActiveQuery> activeQueries = new ArrayList<BestFirstActiveQuery>();
    private final int NumberOfRequiredSuggestions = 4;

    public TriePriorityTraverser(Trie<Document> root){
        BestFirstActiveQuery rootActiveNode = new BestFirstActiveQuery(root);
        activeQueries.add(rootActiveNode);
    }

    public ArrayList<String> addCharacter(char character, int numberOfRequiredSuggestions){
        ArrayList<BestFirstActiveQuery> nextActiveQueries = new ArrayList<BestFirstActiveQuery>();
        ArrayList<String> suggestions = new ArrayList<String>(numberOfRequiredSuggestions);
        for(BestFirstActiveQuery currentBestActiveQuery : activeQueries){
            boolean hasFinished = handleActiveQuery(
                    currentBestActiveQuery,
                    character,
                    nextActiveQueries,
                    suggestions);

            if(hasFinished){
                break;
            }
        }

        return suggestions;
    }

    public boolean handleActiveQuery(
            BestFirstActiveQuery activeQuery,
            char character,
            ArrayList<BestFirstActiveQuery> nextActiveQueries,
            ArrayList<String> suggestions)
    {
        while(activeQuery != null){
            if(activeQuery.isExhausted()){
                activeQuery.getSuggestions(suggestions);
                if(suggestions.size() >= NumberOfRequiredSuggestions){
                    break;
                }
            }
            else{
                activeQuery = activeQuery.getBestNextActiveNode(character);
            }
        }

        return true;
    }
}
