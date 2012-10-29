package Query;

import Index.Index;
import Query.ActiveQuery;

import java.util.ArrayList;

public class InteractiveSearchHandler{

    private ArrayList<ActiveQuery> activeQueries = new ArrayList<ActiveQuery>();
    private String activeQueryString = "";
    private Index index;
    private TriePriorityTraverser query;
    private ArrayList<String> suggestions = new ArrayList<String>();
    private QueryString queryString = new QueryString();


    public InteractiveSearchHandler(Index index){
        this.index = index;
        query = index.initFastInteractiveSearch(queryString);
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
            suggestions = new ArrayList<String>();
        }
    }

    public void handleUserInput(String queryString){
        if(!queryString.equals(activeQueryString)){
            if(queryString.startsWith(activeQueryString)){
                addCharacter(queryString);
            }
            else{
                query = index.initFastInteractiveSearch(this.queryString);
                activeQueries.add(index.initInteractiveSearch());
                addCharacter(queryString);
            }

            activeQueryString = queryString;
        }
    }

    public ArrayList<String> getSearchResults() {
        return suggestions;
    }
}
