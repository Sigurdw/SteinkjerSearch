package Query;

import Index.Index;

import java.util.ArrayList;

public class InteractiveSearchHandler{

    private String activeQueryString = "";
    private Index index;
    private ITrieTraverser query;
    private ArrayList<ISuggestionWrapper> suggestions = new ArrayList<ISuggestionWrapper>();
    private QueryString queryString = new QueryString();

    private boolean isPlain = false;


    public InteractiveSearchHandler(Index index){
        this.index = index;
        initInteractiveSearch();
    }

    private void initInteractiveSearch() {
        if(isPlain){
            query = index.initInteractiveSearch(queryString);
        }
        else{
            query = index.initFastInteractiveSearch(queryString);
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
        for (ISuggestionWrapper suggestion : suggestions){
            suggestionStrings.add(suggestion.toString());
        }

        return suggestionStrings;
    }
}
