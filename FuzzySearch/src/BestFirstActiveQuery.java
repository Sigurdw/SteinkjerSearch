import com.sun.org.apache.xerces.internal.impl.xs.SubstitutionGroupHandler;

import java.util.ArrayList;
import java.util.Map;
import java.util.PriorityQueue;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        10.10.12
 * Time:        12:31
 * To change this template use File | Settings | File Templates.
 */
public class BestFirstActiveQuery {
    private Trie<Document> queryPosition;
    private int previousEdits;
    private char character = 0;
    private EditOperation lastEditOperation;
    private int nextChild = 0;
    private Backlink backlink;

    private PriorityQueue<Backlink> forwardLinks = new PriorityQueue<Backlink>();

    private BestFirstActiveQuery(
            Trie<Document> queryPosition,
            int previousEdits,
            EditOperation lastEditOperation,
            Backlink backlink
            )
    {
        this.queryPosition = queryPosition;
        this.previousEdits = previousEdits;
        this.lastEditOperation = lastEditOperation;
        this.backlink = backlink;
    }

    public BestFirstActiveQuery(Trie<Document> rootNode){
        this(rootNode, 0, EditOperation.Insert, new Backlink(-1, null));
    }

    public String toString(){
        return "ActiveQuery: " + queryPosition.getLabel() + " Last operation: " + lastEditOperation + " previous edits: " + previousEdits + " rank; " + queryPosition.getRank();
    }

    public String getLabel() {
        return queryPosition.getLabel();
    }

    public boolean isExhausted() {
        return character == 0;
    }

    public BestFirstActiveQuery getBestNextActiveNode(char character) {
        System.out.println("Getting the best node.");
        this.character = character;
        Trie<Document> match = queryPosition.getChildren().get(character);
        double[] ranks = {0, 0, backlink.getRank()};
        if(match != null){
            ranks[0] = match.getRank();
            System.out.println("Got match with label: " + match.getLabel() + " and rank: " + match.getRank());
        }

        Trie<Document> bestNode = null;
        if(nextChild < queryPosition.getSize()){
            bestNode = queryPosition.getOrderedChild(nextChild);
        }

        if(bestNode != null){
            System.out.println("Got best with label: " + bestNode.getLabel() + " and rank: " + bestNode.getRank());
            if(bestNode == match){
                ranks[1] = -1;
                nextChild++;
            }
            else{
                ranks[1] = bestNode.getRank() * 0.5;
            }
        }

        ranks[2] = backlink.getRank();

        BestFirstActiveQuery nextActiveQuery = null;
        Backlink newBacklink;

        switch (argMax(ranks)){
            case 0:
                newBacklink = new Backlink(Math.max(ranks[1], ranks[2]), this);
                nextActiveQuery = new BestFirstActiveQuery(match, previousEdits, EditOperation.Insert, newBacklink);
                break;
            case 1:
                newBacklink = new Backlink(Math.max(ranks[0], ranks[2]), this);
                nextActiveQuery = new BestFirstActiveQuery(bestNode, previousEdits + 1, EditOperation.Substitution, newBacklink);
                nextChild++;
                break;
            case 2:
                newBacklink = new Backlink(ranks[1], this);
                nextActiveQuery = backlink.getActiveQuery();
                nextActiveQuery.addLink(newBacklink);
        }

        return nextActiveQuery;
    }

    private int argMax(double[] list){
        int maxIndex = 0;
        for(int i = 1; i < list.length; i++){
            if(list[i] > list [maxIndex]){
                maxIndex = i;
            }
        }

        return maxIndex;
    }

    private void addLink(Backlink backlink){
        forwardLinks.add(backlink);
    }

    public void getSuggestions(ArrayList<String> suggestionList){
        //Testcode
        suggestionList.add(toString());
    }

    public BestFirstActiveQuery travelTheBacklink() {
        System.out.println("Traveling the backlink.");
        BestFirstActiveQuery backnode = backlink.getActiveQuery();
        //todo get the actual next rank:
        backnode.addLink(new Backlink(queryPosition.getRank() / 10, this));
        return backnode;
    }
}
