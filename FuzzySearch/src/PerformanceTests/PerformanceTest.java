package PerformanceTests;

import DocumentModel.IDocument;
import Index.Crawler;
import Index.Index;
import Index.Indexer;
import Query.InteractiveSearchHandler;

import java.util.ArrayList;

public class PerformanceTest {
    private static final String directoryPath = "D:/TextCollection";
    private static final int numberOfSuggestionsRequired = 10;
    private static final int allowedEditDistance = 2;
    private Index index;
    private Index priorityIndex;

    public PerformanceTest(){
        Crawler crawler = new Crawler(directoryPath);
        ArrayList<IDocument> documents = crawler.crawlDirectory();
        Indexer naiveIndexer = new Indexer(numberOfSuggestionsRequired, true);
        index = naiveIndexer.indexDocuments(documents);
        //Indexer priorityIndexer = new Indexer(numberOfSuggestionsRequired, true);
        //priorityIndex =
    }

    public void runTest(){
        ArrayList<String> terms = index.getRandomIndexTerms(100);
        ArrayList<String> modifiedTerms = TermModifier.introduceModifications(terms, 2);
        long totalNaiveTime = 0;
        long totalPriorityTime = 0;
        for(String modifiedTerm : modifiedTerms){
            System.out.println("Doing naive search for " + modifiedTerm);
            InteractiveSearchHandler naiveSearchHandler = new InteractiveSearchHandler(index, 10, false, 2);
            long naiveTime = doInteractiveSearch(naiveSearchHandler, modifiedTerm);
            System.out.println(naiveSearchHandler.getSearchResults());

            System.out.println();

            System.out.println("Doing priority search for " + modifiedTerm);
            InteractiveSearchHandler prioritySearchHandler = new InteractiveSearchHandler(index, 10, true, 2);
            long priorityTime = doInteractiveSearch(prioritySearchHandler, modifiedTerm);

            System.out.println(prioritySearchHandler.getSearchResults());

            System.out.println("Naive node usage: " + naiveSearchHandler.getTotalNodes() + ", priority: " + prioritySearchHandler.getTotalNodes() + ", " + (100 * (double)prioritySearchHandler.getTotalNodes() / (double)naiveSearchHandler.getTotalNodes()) + "%" );
            System.out.println("Naive time: " + naiveTime + " , Priority time " + priorityTime + ", Difference: " + (naiveTime - priorityTime) + " ,Percentage: " + (100 * ((double)priorityTime / (double)naiveTime)));

            System.out.println();
            totalNaiveTime += naiveTime;
            totalPriorityTime += priorityTime;
        }

        System.out.println("Total percentage: " + (100 * ((double)totalPriorityTime / (double)totalNaiveTime)));
    }

    public void individualCharacterIterationTest(){
        int numberOfEdits = 1;
        ArrayList<String> terms = index.getRandomIndexTerms(1000);
        ArrayList<String> modifiedTerms = TermModifier.introduceModifications(terms, numberOfEdits);
        long totalNaiveTime = 0;
        long totalPriorityTime = 0;
        for(String modifiedTerm : modifiedTerms){
            System.out.println("Doing naive search for " + modifiedTerm);
            InteractiveSearchHandler naiveSearchHandler = new InteractiveSearchHandler(index, 10, false, numberOfEdits);
            long naiveTime = doInteractiveSearch(naiveSearchHandler, modifiedTerm);
            System.out.println(naiveSearchHandler.getSearchResults());

            System.out.println();

            System.out.println("Doing priority search for " + modifiedTerm);
            InteractiveSearchHandler prioritySearchHandler = new InteractiveSearchHandler(index, 10, true, numberOfEdits);
            long priorityTime = doInteractiveSearch(prioritySearchHandler, modifiedTerm);

            System.out.println(prioritySearchHandler.getSearchResults());

            System.out.println("Naive node usage: " + naiveSearchHandler.getTotalNodes() + ", priority: " + prioritySearchHandler.getTotalNodes() + ", " + (100 * (double)prioritySearchHandler.getTotalNodes() / (double)naiveSearchHandler.getTotalNodes()) + "%" );
            System.out.println("Naive time: " + naiveTime + " , Priority time " + priorityTime + ", Difference: " + (naiveTime - priorityTime) + " ,Percentage: " + (100 * ((double)priorityTime / (double)naiveTime)));

            System.out.println();
            totalNaiveTime += naiveTime;
            totalPriorityTime += priorityTime;
        }

        System.out.println("Total percentage: " + (100 * ((double)totalPriorityTime / (double)totalNaiveTime)));
    }

    public void randomCharacterTest(){
        int numberOfEdits = 2;
        ArrayList<String> terms = index.getRandomIndexTerms(10000);
        long totalNaiveTime = 0;
        long totalPriorityTime = 0;
        for(String term : terms){
            String modifiedTerm = TermModifier.scrambleTerm(term);
            System.out.println("Doing naive search for " + modifiedTerm);
            InteractiveSearchHandler naiveSearchHandler = new InteractiveSearchHandler(index, 10, false, numberOfEdits);
            long naiveTime = doInteractiveSearch(naiveSearchHandler, modifiedTerm);
            System.out.println(naiveSearchHandler.getSearchResults());

            System.out.println();

            System.out.println("Doing priority search for " + modifiedTerm);
            InteractiveSearchHandler prioritySearchHandler = new InteractiveSearchHandler(index, 10, true, numberOfEdits);
            long priorityTime = doInteractiveSearch(prioritySearchHandler, modifiedTerm);

            System.out.println(prioritySearchHandler.getSearchResults());

            System.out.println("Naive node usage: " + naiveSearchHandler.getTotalNodes() + ", priority: " + prioritySearchHandler.getTotalNodes() + ", " + (100 * (double)prioritySearchHandler.getTotalNodes() / (double)naiveSearchHandler.getTotalNodes()) + "%" );
            System.out.println("Naive time: " + naiveTime + " , Priority time " + priorityTime + ", Difference: " + (naiveTime - priorityTime) + " ,Percentage: " + (100 * ((double)priorityTime / (double)naiveTime)));

            System.out.println();
            totalNaiveTime += naiveTime;
            totalPriorityTime += priorityTime;
        }

        System.out.println("Total percentage: " + (100 * ((double)totalPriorityTime / (double)totalNaiveTime)));
    }

    private static long doInteractiveSearch(InteractiveSearchHandler searchHandler, String term){
        long totalTime = 0;
        for(int i = 1; i <= term.length(); i++){
            String queryString = term.substring(0, i);
            long startTime = System.nanoTime();
            searchHandler.handleUserInput(queryString);
            long endTime = System.nanoTime();
            System.out.println("Time usage for " + queryString + " was: " + (endTime - startTime));
            totalTime += endTime - startTime;
            System.out.println("Node usage was: " + searchHandler.getNumberOfNodesInLastIteration());
        }

        return totalTime;
    }

    public static void main(String[] args){
        PerformanceTest performanceTest = new PerformanceTest();
        performanceTest.runTest();
    }
}
