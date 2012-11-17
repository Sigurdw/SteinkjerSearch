package Index;

import DataStructure.Trie;
import DocumentModel.IDocument;
import Query.*;
import Query.FastInteractiveSearch.FastActiveNode;
import Query.FastInteractiveSearch.FastTrieTraverser;

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

    public NaiveTrieTraverser initSearch(
            QueryString queryString,
            int numberOfSuggestions,
            int allowedEditDistance){
        ActiveQuery activeQuery = new ActiveQuery(indexImplementation, queryString, allowedEditDistance);
        return new NaiveTrieTraverser(activeQuery, numberOfSuggestions);
    }

    public ITrieTraverser initFastSearch(QueryString queryString, int numberOfSuggestions, int allowedEditDistance) {
        FastActiveNode activeNode = new FastActiveNode(indexImplementation, queryString, allowedEditDistance);
        return new FastTrieTraverser(activeNode, queryString, numberOfSuggestions);
    }
}
