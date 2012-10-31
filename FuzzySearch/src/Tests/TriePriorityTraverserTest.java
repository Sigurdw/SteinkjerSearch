package Tests;

import DataStructure.Trie;
import DocumentModel.IDocument;
import Query.QueryString;
import Query.TriePriorityTraverser;
import org.junit.Test;

import java.util.ArrayList;

public class TriePriorityTraverserTest {

    private Trie<IDocument> root = new Trie<IDocument>();


    @Test
    public void testAddCharacter() throws Exception {
        QueryString queryString = new QueryString();
        TestDocument doc1 = new TestDocument("TestDoc1", null, null);
        root.addKeyDataPair("aa", doc1);
        root.addKeyDataPair("aa", doc1);
        root.addKeyDataPair("aa", doc1);

        root.addKeyDataPair("ba", doc1);
        root.addKeyDataPair("ba", doc1);

        root.addKeyDataPair("bb", doc1);
        root.addKeyDataPair("ab", doc1);

        TriePriorityTraverser traverser = new TriePriorityTraverser(root, queryString);
        queryString.SetQueryString("b");
        ArrayList<String> suggestion = traverser.addCharacter();

        queryString.SetQueryString("ba");
        suggestion = traverser.addCharacter();

        traverser = new TriePriorityTraverser(root, queryString);
        ArrayList<String> noCacheSuggestions = traverser.addCharacter();

        assert suggestion.size() == noCacheSuggestions.size();
        for(int i = 0; i < suggestion.size(); i++){
            assert suggestion.get(i).equals(noCacheSuggestions.get(i));
        }
    }
}
