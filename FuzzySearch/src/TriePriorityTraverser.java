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
    private BestFirstActiveQuery rootActiveNode;
    private final int NumberOfRequiredSuggestions = 4;

    public TriePriorityTraverser(Trie<Document> root){
        rootActiveNode = new BestFirstActiveQuery(root);
    }

    public ArrayList<String> addCharacter(char character, int numberOfRequiredSuggestions){
        ArrayList<String> suggestions = new ArrayList<String>(numberOfRequiredSuggestions);
        BestFirstActiveQuery activeQuery = rootActiveNode.getBestNextActiveNode(character);
        BestFirstActiveQuery previousExhaustedActiveQuery = null;

        while(activeQuery != null){
            System.out.println("inner iteration on " + character);
            if(activeQuery.isExhausted()){
                activeQuery.getSuggestions(suggestions);
                if(previousExhaustedActiveQuery != null){
                    previousExhaustedActiveQuery.addLink(new Backlink(activeQuery.getRank(), activeQuery));
                }
                else{
                    System.out.println("Setting new root: " + activeQuery);
                    rootActiveNode = activeQuery;
                }

                previousExhaustedActiveQuery = activeQuery;
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

        return suggestions;
    }
}
