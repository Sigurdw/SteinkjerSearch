package Tests;

import DataStructure.Trie;
import org.junit.Test;

import java.util.ArrayList;

/**
* Created with IntelliJ IDEA.
* Copywrite:   Sigurd Wien
* User:        Sigurd
* Date:        19.10.12
* Time:        20:30
* To change this template use File | Settings | File Templates.
*/
public class TrieTest {
    Trie<Object> trie = new Trie<Object>(4, true);

    @Test
    public void testAddKeyDataPair() throws Exception {

    }

    @Test
    public void testGetOrderedChild() throws Exception {

    }

    @Test
    public void testGetCachedSuggestions() throws Exception {
        Object document1 = new Object();
        Object document2 = new Object();
        trie.addKeyDataPair("hello", document1);
        ArrayList<Trie<Object>> suggestions = trie.getCachedSuggestions();
        assert suggestions.get(0).getLabel().equals("hello");

        trie.addKeyDataPair("hello", document1);
        trie.addKeyDataPair("hello", document2);
        trie.addKeyDataPair("hi", document2);
        for(Trie<Object> suggestion : suggestions){
            System.out.println(suggestion);
        }

        assert suggestions.get(0).getLabel().equals("hello");
        assert suggestions.get(1).getLabel().equals("hi");
        assert suggestions.size() == 2;
    }

    @Test
    public void testGetSuggestions() throws Exception {

    }

    @Test
    public void testGetChildren() throws Exception {

    }
}
