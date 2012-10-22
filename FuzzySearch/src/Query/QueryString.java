package Query;

/**
 * Created with IntelliJ IDEA.
 * User: t-swien
 * Date: 10/18/12
 * Time: 11:54 AM
 * To change this template use File | Settings | File Templates.
 */
public class QueryString {
    private String queryString;

    public QueryString(){
        queryString = "";
    }

    public void SetQueryString(String queryString){
        this.queryString = queryString;
        System.out.println("Query is now: " + queryString);
    }

    public char GetCharacter(int queryStringIndex){
        char character = 0;
        if(!IsExhausted(queryStringIndex)){
            character = queryString.charAt(queryStringIndex);
        }

        System.out.println("Returning character: " + character + " for index: " + queryStringIndex + " query was: " + queryString);
        return character;
    }

    public boolean IsExhausted(int queryStringIndex){
        return queryString.length() <= queryStringIndex || queryStringIndex < 0;
    }
}
