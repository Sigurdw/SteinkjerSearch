package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;

import java.util.ArrayList;

public class TriePriorityTraverser implements ILinkDiscarder, ITrieTraverser {
    private ActivePriorityNode rootActiveNode;
    private final int NumberOfRequiredSuggestions = 4;
    private QueryString queryString;
    private ArrayList<IDiscardableLink> discardedLinks = new ArrayList<IDiscardableLink>();

    public TriePriorityTraverser(Trie<IDocument> root, QueryString queryString){
        this.queryString = queryString;
        rootActiveNode = new ActivePriorityNode(root, queryString, this);
    }

    public ArrayList<ISuggestionWrapper> addCharacter(){
        //MaybeUsePreviouslyDiscardedLinks();

        ActivePriorityNode activePriorityNode = rootActiveNode.getBestNextActiveNode();
        ArrayList<ActivePriorityNode> exhaustedNodes = new ArrayList<ActivePriorityNode>();
        ActivePriorityNode lastNode = null;

        ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
        int numberOfIterations = 0;
        while(activePriorityNode != null){
            numberOfIterations++;
            System.out.println(
                    "inner iteration " + numberOfIterations + " on " + queryString.GetLastCharacter());
            if(activePriorityNode.isExhausted()){
                int suggestionsNeeded = NumberOfRequiredSuggestions - suggestions.size();
                activePriorityNode.getSuggestions(suggestions, suggestionsNeeded);

                if(!exhaustedNodes.contains(activePriorityNode)){
                    exhaustedNodes.add(activePriorityNode);
                }

                lastNode = activePriorityNode;
            }

            if(suggestions.size() >= NumberOfRequiredSuggestions){
                break;
            }

            activePriorityNode = activePriorityNode.getBestNextActiveNode();
        }

        makeStarCache(exhaustedNodes, lastNode);

        System.out.println("The number of exhausted nodes was: " + exhaustedNodes.size());
        System.out.println("The number of iterations was: " + numberOfIterations);

        return suggestions;
    }

    @Override
    public int getNumberOfNodesInLastIteration() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public int getTotalNodes() {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    private void MaybeUsePreviouslyDiscardedLinks() {
        ArrayList<IDiscardableLink> tempDiscardedList = new ArrayList<IDiscardableLink>();
        for(IDiscardableLink link : discardedLinks){
            if(link.isValid(queryString)){
                link.setSource(rootActiveNode);
                rootActiveNode.addLink((Link)link);
            }
            else{
                tempDiscardedList.add(link);
            }
        }

        discardedLinks = tempDiscardedList;
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

    private void makeStarCache(ArrayList<ActivePriorityNode> exhaustedNodes, ActivePriorityNode lastNode) {
        if(exhaustedNodes.size() < 1){
            System.out.println("No results found");
            return;
        }

        ActivePriorityNode rootNode = exhaustedNodes.get(0);
        BackLink backLink = lastNode.stealBacklink();
        if(backLink != null){
            backLink.setSource(rootNode);
            rootNode.addLink(backLink);
        }

        for(int i = 1; i < exhaustedNodes.size(); i++){
            ActivePriorityNode currentExhaustedNode = exhaustedNodes.get(i);

            ShortcutLink shortcutLink = new ShortcutLink(
                    currentExhaustedNode.getRank(),
                    rootNode,
                    currentExhaustedNode);

            rootNode.addLink(shortcutLink);
            currentExhaustedNode.ignoreBackLinks();
        }

        if(exhaustedNodes.size() > 0){
            rootActiveNode = exhaustedNodes.get(0);
        }
    }

    public void discardLink(IDiscardableLink link){
        System.out.println("Discarding link " + link);
        discardedLinks.add(link);
    }
}
