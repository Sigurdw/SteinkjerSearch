import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        23.09.12
 * Time:        20:05
 * To change this template use File | Settings | File Templates.
 */

public class Indexer {

    public Indexer(){

    }

    public Index indexDocuments(ArrayList<Document> documents){
        Trie<Document> indexImplementation = new Trie<Document>();
        for(Document document : documents){
            List<String> tokens = document.getTokens();
            List<String> indexTerms = getIndexTerms(tokens);
            for(String indexTerm : indexTerms){
                indexImplementation.addKeyDataPair(indexTerm, document);
            }
        }

        System.out.println(
                "Successfully indexed " + indexImplementation.getNumberOfEntries() + " different index terms.");
        return new Index(indexImplementation);
    }

    private List<String> getIndexTerms(List<String> tokens) {
        ArrayList<String> indexTerms = new ArrayList<String>();
        for(String token : tokens){
            String indexTerm = token.toLowerCase();
            indexTerm = removeSigns(indexTerm);

            if(indexTerm.length() > 0){
                indexTerms.add(indexTerm);
            }
            else{
                System.out.println("Token " + token + " was ignored under processing");
            }
        }

        return indexTerms;
    }

    private String removeSigns(String indexTerm) {
        char lastCharacter = indexTerm.charAt(indexTerm.length() - 1);
        if(!Character.isLetterOrDigit(lastCharacter)){
            System.out.println("Removing sign from " + indexTerm);
            return indexTerm.substring(0, indexTerm.length() - 1);
        }

        return indexTerm;
    }
}
