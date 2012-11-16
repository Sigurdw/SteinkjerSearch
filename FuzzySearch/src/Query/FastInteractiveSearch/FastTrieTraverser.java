package Query.FastInteractiveSearch;

import Query.*;

import java.util.ArrayList;
import java.util.PriorityQueue;

public class FastTrieTraverser implements ITrieTraverser {

    private QueryString queryString;
    private int neededSuggestions;
    private PriorityQueue<Link> linkQueue = new PriorityQueue<Link>();
    private ArrayList<FastActiveNode> exhaustedNodes = new ArrayList<FastActiveNode>();

    public FastTrieTraverser(FastActiveNode rootActiveNode, QueryString queryString, int neededSuggestions){
        this.queryString = queryString;
        this.neededSuggestions = neededSuggestions;
        exhaustedNodes.add(rootActiveNode);
    }

    public ArrayList<ISuggestionWrapper> addCharacter() {
        ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
        initiateFromExhaustedNodes();
        Link nextLink = linkQueue.poll();
        int iterationNumber = 1;
        while(neededSuggestions - suggestions.size() > 0 && nextLink != null){
            System.out.println("Iteration " + iterationNumber + " on " + queryString.GetLastCharacter());
            FastActiveNode currentNode = nextLink.UseLink(linkQueue);
            if(currentNode.isExhausted()){
                currentNode.getSuggestions(
                        suggestions,
                        neededSuggestions - suggestions.size(),
                        linkQueue.peek().getRank());

                SuggestionLink suggestionLink = currentNode.getSuggestionLink();
                linkQueue.add(suggestionLink);
                exhaustedNodes.add(currentNode);
            }
            else{
                currentNode.extractLinks(linkQueue);
            }

            if(suggestions.size() >= neededSuggestions){
                break;
            }

            nextLink = linkQueue.poll();
            iterationNumber++;
        }

        System.out.println("Done in " + iterationNumber);

        return suggestions;
    }

    private void initiateFromExhaustedNodes() {
        for(FastActiveNode exhaustedNode : exhaustedNodes){
            exhaustedNode.extractLinks(linkQueue);
        }

        exhaustedNodes.clear();
    }
}
