package Query;

import java.util.ArrayList;
import java.util.Collections;

public class NaiveTrieTraverser implements ITrieTraverser {

    private ArrayList<ActiveQuery> activeQueries = new ArrayList<ActiveQuery>();
    private final int numberOfSuggestions;

    public NaiveTrieTraverser(ActiveQuery activeQuery, int numberOfSuggestions){
        this.numberOfSuggestions = numberOfSuggestions;
        activeQueries.add(activeQuery);
    }

    @Override
    public ArrayList<ISuggestionWrapper> addCharacter() {
        //System.out.println("Iteration on " + activeQueries.size() + " active nodes.");
        ArrayList<ActiveQuery> nextActiveQueries = new ArrayList<ActiveQuery>();
        for(ActiveQuery activeQuery : activeQueries){
            activeQuery.addCharacter(nextActiveQueries);
        }

        ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
        for(ActiveQuery activeQuery : nextActiveQueries){
            activeQuery.getSuggestions(suggestions);
        }

        Collections.sort(suggestions);

        activeQueries = nextActiveQueries;
        System.out.println("Iteration completed: " + activeQueries.size());

        return suggestions;
    }
}
