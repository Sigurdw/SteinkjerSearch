package Index;

import DataStructure.Trie;
import DocumentModel.IDocument;
import Query.ActiveQuery;
import Query.EditOperation;
import Query.QueryString;
import Query.TriePriorityTraverser;

import java.util.ArrayList;

public class Index {
    private Trie<IDocument> indexImplementation;

    public Index(Trie<IDocument> indexImplementation){
        this.indexImplementation = indexImplementation;
    }

    public ArrayList<IDocument> search(String key){
        System.out.println("Searching the index for: " + key);
        return indexImplementation.search(key);
    }

    public ArrayList<String> getSuggestions(String partialKey){
        System.out.println("Getting suggestions for: " + partialKey);
        return indexImplementation.getSuggestions(partialKey);
    }

    public ActiveQuery initInteractiveSearch(){
        return new ActiveQuery(indexImplementation, 1, EditOperation.Match);
    }

    public TriePriorityTraverser initFastInteractiveSearch(QueryString queryString){
        return new TriePriorityTraverser(indexImplementation, queryString);
    }
}
