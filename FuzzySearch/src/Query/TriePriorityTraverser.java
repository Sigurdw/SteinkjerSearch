package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;
import java.util.ArrayList;

public class TriePriorityTraverser {
    private ActivePriorityNode rootActiveNode;
    private final int NumberOfRequiredSuggestions = 3;

    public TriePriorityTraverser(Trie<IDocument> root, QueryString queryString){
        rootActiveNode = new ActivePriorityNode(root, queryString);
    }

    public ArrayList<String> addCharacter(char character, int numberOfRequiredSuggestions){
        ArrayList<String> suggestions = new ArrayList<String>(numberOfRequiredSuggestions);
        ActivePriorityNode activePriorityNode = rootActiveNode.getBestNextActiveNode();
        ActivePriorityNode previousExhaustedActivePriorityNode = null;

        int numberOfIterations = 0;
        while(activePriorityNode != null){
            numberOfIterations++;
            System.out.println("inner iteration on " + character);
            if(activePriorityNode.isExhausted()){
                activePriorityNode.getSuggestions(suggestions, NumberOfRequiredSuggestions - suggestions.size());
                if(previousExhaustedActivePriorityNode != null){
                    ShortcutLink shortcutLink = new ShortcutLink(
                            activePriorityNode.getRank(),
                            previousExhaustedActivePriorityNode,
                            activePriorityNode);
                    previousExhaustedActivePriorityNode.addLink(shortcutLink);
                    previousExhaustedActivePriorityNode.ignoreBackLinks();
                }
                else{
                    rootActiveNode = activePriorityNode;
                }

                previousExhaustedActivePriorityNode = activePriorityNode;
            }

            if(suggestions.size() >= NumberOfRequiredSuggestions){
                break;
            }

            activePriorityNode = activePriorityNode.getBestNextActiveNode();
        }

        System.out.println("The number of iterations was: " + numberOfIterations);

        return suggestions;
    }
}
