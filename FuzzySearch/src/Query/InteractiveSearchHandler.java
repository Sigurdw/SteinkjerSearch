package Query;

import Index.Index;

import java.util.ArrayList;

public class InteractiveSearchHandler{

    private String activeQueryString = "";
    private Index index;
    private int numberOfSuggestionsRequired;
    private boolean usePrioritySearch;
    private int allowedEditDistance;
    private ITrieTraverser query;
    private ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
    private QueryString queryString = new QueryString();

    public InteractiveSearchHandler(
            Index index,
            int numberOfSuggestionsRequired,
            boolean usePrioritySearch,
            int allowedEditDistance)
    {
        this.index = index;
        this.numberOfSuggestionsRequired = numberOfSuggestionsRequired;
        this.usePrioritySearch = usePrioritySearch;
        this.allowedEditDistance = allowedEditDistance;
        initInteractiveSearch();
    }

    private void initInteractiveSearch() {
        if(usePrioritySearch){
            query = index.initFastSearch(queryString, numberOfSuggestionsRequired, allowedEditDistance);
        }
        else{
            query = index.initSearch(queryString, numberOfSuggestionsRequired, allowedEditDistance);
        }
    }


    private void addCharacter(String queryStr){
        queryString.SetQueryString(queryStr);
        char lastCharacter = queryString.GetLastCharacter();
        if(lastCharacter != 0){
            System.out.println("Got: " + lastCharacter);
            suggestions = query.addCharacter();
            System.out.println(suggestions);
        }
        else{
            suggestions = new ArrayList<ISuggestionWrapper>();
        }
    }

    public void handleUserInput(String queryString){
        if(!queryString.equals(activeQueryString)){
            if(queryString.startsWith(activeQueryString)){
                addCharacter(queryString);
            }
            else{
                initInteractiveSearch();
                addCharacter(queryString);
            }

            activeQueryString = queryString;
        }
    }

    public ArrayList<String> getSearchResults() {
        ArrayList<String> suggestionStrings = new ArrayList<String>(suggestions.size());
        for (int i = 0; i < Math.min(numberOfSuggestionsRequired, suggestions.size()); i++){
            suggestionStrings.add(suggestions.get(i).toString());
        }

        return suggestionStrings;
    }
}
