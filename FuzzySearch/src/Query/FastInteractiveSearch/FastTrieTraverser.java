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
        Link nextLink = getNextLink();
        int iterationNumber = 1;
        while(neededSuggestions - suggestions.size() > 0 && nextLink != null){
            //System.out.println("Iteration " + iterationNumber + " on " + queryString.GetLastCharacter());
            FastActiveNode currentNode = nextLink.UseLink(linkQueue);
            if(currentNode.isExhausted()){
                double thresholdRank = 0;
                Link thresholdLink = linkQueue.peek();
                if(thresholdLink != null){
                    thresholdRank = thresholdLink.getRank();
                }

                currentNode.getSuggestions(
                        suggestions,
                        neededSuggestions - suggestions.size(),
                        thresholdRank);

                SuggestionLink suggestionLink = currentNode.getSuggestionLink();
                if(suggestionLink != null){
                    linkQueue.add(suggestionLink);
                }

                if(!exhaustedNodes.contains(currentNode)){
                    exhaustedNodes.add(currentNode);
                }
            }
            else{
                currentNode.extractLinks(linkQueue);
            }

            if(suggestions.size() >= neededSuggestions){
                break;
            }

            nextLink = getNextLink();
            iterationNumber++;
        }

        System.out.println("Done in " + iterationNumber);

        return suggestions;
    }

    private Link getNextLink() {
        Link nextLink = linkQueue.poll();
        while(nextLink != null && !nextLink.isValid(queryString)){
            nextLink = linkQueue.poll();
        }

        return nextLink;
    }

    private void initiateFromExhaustedNodes() {
        for(FastActiveNode exhaustedNode : exhaustedNodes){
            exhaustedNode.extractLinks(linkQueue);
        }

        exhaustedNodes.clear();
    }
}
