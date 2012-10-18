import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        23.09.12
 * Time:        20:09
 * To change this template use File | Settings | File Templates.
 */
public class Index {
    private Trie<Document> indexImplementation;

    public Index(Trie<Document> indexImplementation){
        this.indexImplementation = indexImplementation;
    }

    ArrayList<Document> search(String key){
        System.out.println("Searching the index for: " + key);
        return indexImplementation.search(key);
    }

    ArrayList<String> getSuggestions(String partialKey){
        System.out.println("Getting suggestions for: " + partialKey);
        return indexImplementation.getSuggestions(partialKey);
    }

    ActiveQuery initInteractiveSearch(){
        return new ActiveQuery(indexImplementation, 1, EditOperation.Insert);
    }

    public TriePriorityTraverser initFastInteractiveSearch(QueryString queryString){
        return new TriePriorityTraverser(indexImplementation, queryString);
    }
}
