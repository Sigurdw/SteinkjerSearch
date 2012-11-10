package Tests;

import DataStructure.Trie;
import DocumentModel.IDocument;
import Query.ActivePriorityNode;
import Query.EditOperation;
import Query.QueryString;
import org.junit.Test;

import java.util.ArrayList;

public class ActivePriorityNodeTest {
    Trie<IDocument> root = new Trie<IDocument>();

    @Test
    public void testIsExhausted() throws Exception {
        TestDocument document = new TestDocument("TestDoc1", null, null);
        root.addKeyDataPair("Test", document);

        QueryString queryString = new QueryString();
        ActivePriorityNode priorityNodeTest = new ActivePriorityNode(root, queryString, null);
        assert priorityNodeTest.isExhausted();

        queryString.SetQueryString("T");

        assert !priorityNodeTest.isExhausted();

        assert priorityNodeTest.getBestNextActiveNode().isExhausted();
    }

    @Test
    public void testThatBackNodeIsTheBestNodeForAnExhaustedNode() throws Exception {
        TestDocument document = new TestDocument("TestDoc1", null, null);
        root.addKeyDataPair("Test", document);

        QueryString queryString = new QueryString();
        ActivePriorityNode firstNode = new ActivePriorityNode(root, queryString, null);

        queryString.SetQueryString("T");

        ActivePriorityNode secondNode = firstNode.getBestNextActiveNode();

        assert secondNode.isExhausted();
        assert firstNode != secondNode;

        ActivePriorityNode thirdNode = secondNode.getBestNextActiveNode();

        assert thirdNode == firstNode;
    }


    @Test
    public void useEditBeforeBacklinkOnTieTest(){
        TestDocument document = new TestDocument("TestDoc1", null, null);
        root.addKeyDataPair("Test", document);

        QueryString queryString = new QueryString();
        ActivePriorityNode activeNode = new ActivePriorityNode(root, queryString, null);

        queryString.SetQueryString("A");

        activeNode = activeNode.getBestNextActiveNode();
        assert activeNode.isExhausted();
        assert activeNode.getLastOperation() == EditOperation.Delete;

        activeNode = activeNode.getBestNextActiveNode();
        assert !activeNode.isExhausted();
        assert activeNode.getLastOperation() == EditOperation.Match;

        activeNode = activeNode.getBestNextActiveNode();
        assert !activeNode.isExhausted();
        assert activeNode.getLastOperation() == EditOperation.Insert;

        activeNode = activeNode.getBestNextActiveNode();
        assert activeNode.isExhausted();
        assert activeNode.getLastOperation() == EditOperation.Delete;
    }

    @Test
    public void rankHandlingTest() throws Exception {
        QueryString queryString = new QueryString();
        TestDocument doc1 = new TestDocument("TestDoc1", null, null);
        root.addKeyDataPair("aa", doc1);
        root.addKeyDataPair("aa", doc1);
        root.addKeyDataPair("aa", doc1);

        root.addKeyDataPair("ba", doc1);
        root.addKeyDataPair("ba", doc1);

        root.addKeyDataPair("bb", doc1);
        root.addKeyDataPair("ab", doc1);

        ActivePriorityNode activeNode = new ActivePriorityNode(root, queryString, null);

        queryString.SetQueryString("b");

        activeNode = activeNode.getBestNextActiveNode();

        ArrayList<Trie<IDocument>> suggestions = new ArrayList<Trie<IDocument>>();
        activeNode.getSuggestions(suggestions, 3);

        assert  suggestions.size() == 1;
        assert suggestions.get(0).getLabel().equals("ba");

        activeNode = activeNode.getBestNextActiveNode();
        activeNode = activeNode.getBestNextActiveNode();
        activeNode = activeNode.getBestNextActiveNode();
        activeNode.getSuggestions(suggestions, 3);

        assert  suggestions.size() == 2;
        assert suggestions.get(1).getLabel().equals("aa");

        activeNode = activeNode.getBestNextActiveNode();
        activeNode = activeNode.getBestNextActiveNode();
        activeNode = activeNode.getBestNextActiveNode();

        assert activeNode.getLabel().equals("b");

        activeNode.getSuggestions(suggestions, 3);

        assert suggestions.size() == 3;
        assert suggestions.get(2).getLabel().equals("bb");
    }

    @Test
    public void cacheStructureTest(){

    }

    @Test
    public void testAddLink() throws Exception {

    }

    @Test
    public void testGetSuggestions() throws Exception {

    }

    @Test
    public void testTravelTheBacklink() throws Exception {

    }
}
