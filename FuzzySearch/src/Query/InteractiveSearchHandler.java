package Query;

import Index.Index;
import Query.ActiveQuery;

import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        29.09.12
 * Time:        15:01
 * To change this template use File | Settings | File Templates.
 */
public class InteractiveSearchHandler{

    private ArrayList<ActiveQuery> activeQueries = new ArrayList<ActiveQuery>();
    private String activeQueryString = "";
    private Index index;
    private TriePriorityTraverser query;
    private ArrayList<String> suggestions = new ArrayList<String>();
    private QueryString queryString = new QueryString();


    public InteractiveSearchHandler(Index index){
        //activeQueries.add(index.initInteractiveSearch());
        this.index = index;
        query = index.initFastInteractiveSearch(queryString);
    }


    private void addCharacter(String queryStr, char character){
        System.out.println("Got: " + character);
        //ArrayList<Query.ActiveQuery> nextActiveQueries = new ArrayList<Query.ActiveQuery>();
        //for(Query.ActiveQuery activeQuery : activeQueries){
        //    activeQuery.addCharacter(character, nextActiveQueries);
        //}

        /*System.out.println(
                "Finished processing "
                + activeQueries.size()
                + " active queries. Now there are "
                + nextActiveQueries.size() + " active queries.");*/

        //for(Query.ActiveQuery nextActiveQuery : nextActiveQueries){
        //    //System.out.println(nextActiveQuery);
        //}

        //activeQueries = nextActiveQueries;
        queryString.SetQueryString(queryStr);
        suggestions = query.addCharacter(character, 1);
        System.out.println(suggestions);
    }

    public void handleUserInput(String queryString){
        if(queryString.startsWith(activeQueryString)){
            for(int i = activeQueryString.length(); i < queryString.length(); i++){
                char queryCharacter = queryString.charAt(i);
                System.out.println("adding char");
                addCharacter(queryString, queryCharacter);
            }
        }
        else{
            activeQueries.clear();
            activeQueries.add(index.initInteractiveSearch());
            for(int i = 0; i < queryString.length(); i++){
                char queryCharacter = queryString.charAt(i);
                addCharacter(queryString, queryCharacter);
            }
        }

        activeQueryString = queryString;
    }

    public ArrayList<String> getSearchResults() {
        /*ArrayList<String> results = new ArrayList<String>();
        for(Query.ActiveQuery activeQuery : activeQueries){
            results.add(activeQuery.getLabel());
        }

        return results;*/
        return suggestions;
    }
}
