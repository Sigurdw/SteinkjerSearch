package Tests;

import DataStructure.Trie;
import DocumentModel.IDocument;
import Query.ActivePriorityNode;
import Query.EditOperation;
import Query.QueryString;
import org.junit.Test;

public class ActivePriorityNodeTest {
    Trie<IDocument> root = new Trie<IDocument>();

    @Test
    public void testIsExhausted() throws Exception {
        TestDocument document = new TestDocument("TestDoc1", null, null);
        root.addKeyDataPair("Test", document);

        QueryString queryString = new QueryString();
        ActivePriorityNode priorityNodeTest = new ActivePriorityNode(root, queryString);
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
        ActivePriorityNode firstNode = new ActivePriorityNode(root, queryString);

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
        ActivePriorityNode activeNode = new ActivePriorityNode(root, queryString);

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
    public void testAddLink() throws Exception {

    }

    @Test
    public void testGetSuggestions() throws Exception {

    }

    @Test
    public void testTravelTheBacklink() throws Exception {

    }
}
