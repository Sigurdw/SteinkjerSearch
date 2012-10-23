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
        ActivePriorityNode activePriorityNode = rootActiveNode.getBestNextActiveNode();
        ActivePriorityNode previousExhaustedActivePriorityNode = null;

        ArrayList<Trie<IDocument>> suggestionNodes = new ArrayList<Trie<IDocument>>();
        int numberOfIterations = 0;
        while(activePriorityNode != null){
            numberOfIterations++;
            System.out.println("inner iteration on " + character);
            if(activePriorityNode.isExhausted()){
                activePriorityNode.getSuggestions(suggestionNodes, NumberOfRequiredSuggestions - suggestionNodes.size());
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

            if(suggestionNodes.size() >= NumberOfRequiredSuggestions){
                break;
            }

            activePriorityNode = activePriorityNode.getBestNextActiveNode();
        }

        System.out.println("The number of iterations was: " + numberOfIterations);

        ArrayList<String> suggestions = new ArrayList<String>(numberOfRequiredSuggestions);
        for(Trie<IDocument> suggestionNode : suggestionNodes){
            suggestions.add(suggestionNode.getLabel());
        }

        return suggestions;
    }
}
