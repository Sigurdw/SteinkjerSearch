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
    private final int NumberOfRequiredSuggestions = 2;

    public TriePriorityTraverser(Trie<Document> root){
        BestFirstActiveQuery rootActiveNode = new BestFirstActiveQuery(root);
        activeQueries.add(rootActiveNode);
    }

    public ArrayList<String> addCharacter(char character, int numberOfRequiredSuggestions){
        ArrayList<BestFirstActiveQuery> nextActiveQueries = new ArrayList<BestFirstActiveQuery>();
        ArrayList<String> suggestions = new ArrayList<String>(numberOfRequiredSuggestions);
        for(BestFirstActiveQuery currentBestActiveQuery : activeQueries){
            System.out.println("iteration on " + character + " " + currentBestActiveQuery);
            boolean hasFinished = handleActiveQuery(
                    currentBestActiveQuery,
                    character,
                    nextActiveQueries,
                    suggestions);

            if(hasFinished){
                break;
            }
        }

        activeQueries = nextActiveQueries;

        return suggestions;
    }

    public boolean handleActiveQuery(
            BestFirstActiveQuery activeQuery,
            char character,
            ArrayList<BestFirstActiveQuery> nextActiveQueries,
            ArrayList<String> suggestions)
    {
        activeQuery = activeQuery.getBestNextActiveNode(character);

        while(activeQuery != null){
            System.out.println("inner iteration on " + character);
            if(activeQuery.isExhausted()){
                activeQuery.getSuggestions(suggestions);
                nextActiveQueries.add(activeQuery);
                if(suggestions.size() >= NumberOfRequiredSuggestions){
                    break;
                }
                else{
                    activeQuery = activeQuery.travelTheBacklink();
                }
            }
            else{
                activeQuery = activeQuery.getBestNextActiveNode(character);
            }
        }

        return true;
    }
}
