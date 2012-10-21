package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;
import Query.ActivePriorityNode;
import Query.Link;
import Query.QueryString;

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
    private ActivePriorityNode rootActiveNode;
    private final int NumberOfRequiredSuggestions = 4;
    private final QueryString queryString;

    public TriePriorityTraverser(Trie<IDocument> root, QueryString queryString){
        rootActiveNode = new ActivePriorityNode(root, queryString);
        this.queryString = queryString;
    }

    public ArrayList<String> addCharacter(char character, int numberOfRequiredSuggestions){
        ArrayList<String> suggestions = new ArrayList<String>(numberOfRequiredSuggestions);
        ActivePriorityNode activePriorityNode = rootActiveNode.getBestNextActiveNode();
        ActivePriorityNode previousExhaustedActivePriorityNode = null;

        while(activePriorityNode != null){
            System.out.println("inner iteration on " + character);
            if(activePriorityNode.isExhausted()){
                activePriorityNode.getSuggestions(suggestions, numberOfRequiredSuggestions - suggestions.size());
                if(previousExhaustedActivePriorityNode != null){
                    System.out.println("Adding shortcut link.");
                    previousExhaustedActivePriorityNode.addLink(
                            new ShortcutLink(
                                    activePriorityNode.getRank(),
                                    activePriorityNode));
                }
                else{
                    System.out.println("Setting new root: " + activePriorityNode);
                    rootActiveNode = activePriorityNode;
                }

                previousExhaustedActivePriorityNode = activePriorityNode;
            }

            if(suggestions.size() >= NumberOfRequiredSuggestions){
                break;
            }
            else{
                activePriorityNode = activePriorityNode.getBestNextActiveNode();
            }
        }

        return suggestions;
    }
}
