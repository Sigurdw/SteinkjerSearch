package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;

import java.util.ArrayList;

public class TriePriorityTraverser {
    private ActivePriorityNode rootActiveNode;
    private final int NumberOfRequiredSuggestions = 4;

    public TriePriorityTraverser(Trie<IDocument> root, QueryString queryString){
        rootActiveNode = new ActivePriorityNode(root, queryString);
    }

    public ArrayList<String> addCharacter(char character, int numberOfRequiredSuggestions){
        ActivePriorityNode activePriorityNode = rootActiveNode.getBestNextActiveNode();
        ArrayList<ActivePriorityNode> exhaustedNodes = new ArrayList<ActivePriorityNode>();

        ArrayList<Trie<IDocument>> suggestionNodes = new ArrayList<Trie<IDocument>>();
        int numberOfIterations = 0;
        while(activePriorityNode != null){
            numberOfIterations++;
            System.out.println("inner iteration on " + character);
            if(activePriorityNode.isExhausted()){
                activePriorityNode.getSuggestions(suggestionNodes, NumberOfRequiredSuggestions - suggestionNodes.size());
                exhaustedNodes.add(activePriorityNode);
            }

            if(suggestionNodes.size() >= NumberOfRequiredSuggestions){
                break;
            }

            activePriorityNode = activePriorityNode.getBestNextActiveNode();
        }

        handleCacheStructure(exhaustedNodes);

        System.out.println("The number of exhausted nodes was: " + exhaustedNodes.size());
        System.out.println("The number of iterations was: " + numberOfIterations);

        ArrayList<String> suggestions = new ArrayList<String>(numberOfRequiredSuggestions);
        for(Trie<IDocument> suggestionNode : suggestionNodes){
            suggestions.add(suggestionNode.getLabel());
        }

        return suggestions;
    }

    private void handleCacheStructure(ArrayList<ActivePriorityNode> exhaustedNodes) {
        for(int i = 1; i < exhaustedNodes.size(); i++){
            ActivePriorityNode previouslyExhaustedNode = exhaustedNodes.get(i - 1);
            ActivePriorityNode currentExhaustedNode = exhaustedNodes.get(i);

            ShortcutLink shortcutLink = new ShortcutLink(
                    currentExhaustedNode.getRank(),
                    previouslyExhaustedNode,
                    currentExhaustedNode);

            previouslyExhaustedNode.addLink(shortcutLink);
            previouslyExhaustedNode.ignoreBackLinks();
        }

        if(exhaustedNodes.size() > 0){
            rootActiveNode = exhaustedNodes.get(0);
        }
    }
}
