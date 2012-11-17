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

    public NaiveTrieTraverser initInteractiveSearch(QueryString queryString, int numberOfSuggestions){
        ActiveQuery activeQuery = new ActiveQuery(indexImplementation, 1, EditOperation.Match, queryString, 0, false);
        return new NaiveTrieTraverser(activeQuery, numberOfSuggestions);
    }

    public TriePriorityTraverser initFastInteractiveSearch(QueryString queryString, int numberOfSuggestions){
        return new TriePriorityTraverser(indexImplementation, queryString);
    }

    public ITrieTraverser initSearch(QueryString queryString, int numberOfSuggestions, int maxEdits) {
        FastActiveNode activeNode = new FastActiveNode(indexImplementation, queryString, maxEdits);
        return new FastTrieTraverser(activeNode, queryString, numberOfSuggestions);
    }
}
