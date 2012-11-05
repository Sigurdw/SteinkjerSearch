package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;

import java.util.ArrayList;

public class TriePriorityTraverser {
    private ActivePriorityNode rootActiveNode;
    private final int NumberOfRequiredSuggestions = 10;
    private QueryString queryString;

    public TriePriorityTraverser(Trie<IDocument> root, QueryString queryString){
        this.queryString = queryString;
        rootActiveNode = new ActivePriorityNode(root, queryString);
    }

    public ArrayList<String> addCharacter(){
        ActivePriorityNode activePriorityNode = rootActiveNode.getBestNextActiveNode();
        ArrayList<ActivePriorityNode> exhaustedNodes = new ArrayList<ActivePriorityNode>();

        ArrayList<Trie<IDocument>> suggestionNodes = new ArrayList<Trie<IDocument>>();
        int numberOfIterations = 0;
        while(activePriorityNode != null){
            numberOfIterations++;
            System.out.println("inner iteration on " + queryString.GetLastCharacter());
            if(activePriorityNode.isExhausted()){
                int suggestionsNeeded = NumberOfRequiredSuggestions - suggestionNodes.size();
                activePriorityNode.getSuggestions(suggestionNodes, suggestionsNeeded);

                if(!exhaustedNodes.contains(activePriorityNode)){
                    exhaustedNodes.add(activePriorityNode);
                }
            }

            if(suggestionNodes.size() >= NumberOfRequiredSuggestions){
                break;
            }

            activePriorityNode = activePriorityNode.getBestNextActiveNode();
        }

        makeStarCache(exhaustedNodes);

        System.out.println("The number of exhausted nodes was: " + exhaustedNodes.size());
        System.out.println("The number of iterations was: " + numberOfIterations);

        ArrayList<String> suggestions = new ArrayList<String>();
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
            System.out.println("Added shortcut " + shortcutLink);
        }

        if(exhaustedNodes.size() > 0){
            rootActiveNode = exhaustedNodes.get(0);
        }
    }

    private void makeStarCache(ArrayList<ActivePriorityNode> exhaustedNodes) {
        ActivePriorityNode rootNode = exhaustedNodes.get(0);
        BackLink backLink = exhaustedNodes.get(exhaustedNodes.size() - 1).stealBacklink();
        backLink.setSource(rootNode);
        rootNode.addLink(backLink);
        for(int i = 1; i < exhaustedNodes.size(); i++){
            ActivePriorityNode currentExhaustedNode = exhaustedNodes.get(i);

            ShortcutLink shortcutLink = new ShortcutLink(
                    currentExhaustedNode.getRank(),
                    rootNode,
                    currentExhaustedNode);

            rootNode.addLink(shortcutLink);
            currentExhaustedNode.ignoreBackLinks();
            System.out.println("Added shortcut " + shortcutLink);
        }

        if(exhaustedNodes.size() > 0){
            rootActiveNode = exhaustedNodes.get(0);
        }
    }
}
