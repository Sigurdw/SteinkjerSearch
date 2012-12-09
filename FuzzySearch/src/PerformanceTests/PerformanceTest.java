package PerformanceTests;

import DocumentModel.IDocument;
import Index.Crawler;
import Index.Index;
import Index.Indexer;
import Query.InteractiveSearchHandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class PerformanceTest {
    private static final String directoryPath = "C:/TextCollection";
    private static final int numberOfSuggestionsRequired = 10;
    private static final int allowedEditDistance = 2;
    private Index index;
    private Index priorityIndex;
    private InteractiveSearchHandler searchHandler;
    private static final String resultPath = "D:/Results/";
    private static final String fileEnding  = ".csv";

    public PerformanceTest(){
        Indexer naiveIndexer = new Indexer(numberOfSuggestionsRequired, true);
        try {
            index = naiveIndexer.indexDocumentBlob(new File(directoryPath + "/medline2004.txt"), 100000);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        //Indexer priorityIndexer = new Indexer(numberOfSuggestionsRequired, true);
        //priorityIndex =
    }

    public void editDistanceScalingTest(){
        int[] numberOfTerms = {10000, 10000, 1000, 50};
        ArrayList<String> terms = index.getRandomIndexTerms(10000);
        doEditDistanceScaleTest(numberOfTerms, terms, "real");
    }

    public void randomCharacterTest(){
        int[] numberOfTerms = {10000, 10000, 1000, 50};
        ArrayList<String> terms = index.getRandomIndexTerms(10000);
        for(int i = 0; i < terms.size(); i++){
            terms.set(i, TermModifier.scrambleTerm(terms.get(i)));
        }

        doEditDistanceScaleTest(numberOfTerms, terms, "scramble");
    }

    private void doEditDistanceScaleTest(int[] numberOfTerms, ArrayList<String> terms, String type) {
        try {
            File simpleAverageResultFile = new File(resultPath + "simpleEditDistanceTest" + type + System.currentTimeMillis() + fileEnding);
            File priorityAverageResultFile = new File(resultPath + "priorityEditDistanceTest" + type + System.currentTimeMillis() + fileEnding);
            BufferedWriter simpleWriter = new BufferedWriter(new FileWriter(simpleAverageResultFile));
            BufferedWriter priorityWriter = new BufferedWriter(new FileWriter(priorityAverageResultFile));
            for(int i = 0; i < numberOfTerms.length; i++){
                System.out.println("EditDistanceScaling: " + i);
                for(int j = 0; j < numberOfTerms[i]; j++){
                    String term = terms.get(j);
                    String modifiedTerm = TermModifier.modifyTerm(i, term);
                    searchHandler = new InteractiveSearchHandler(index, 10, false, i);
                    long simpleTime = doInteractiveSearch(modifiedTerm);
                    searchHandler = new InteractiveSearchHandler(index, 10, true, i);
                    long priorityTime = doInteractiveSearch(modifiedTerm);

                    String simpleRecord = "" + simpleTime;
                    String priorityRecord = "" + priorityTime;
                    if(j == terms.size() - 1){
                        simpleRecord += "\n";
                        priorityRecord += "\n";
                    }
                    else{
                        simpleRecord += ", ";
                        priorityRecord += ", ";
                    }

                    simpleWriter.write(simpleRecord);
                    priorityWriter.write(priorityRecord);
                }
            }

            simpleWriter.close();
            priorityWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void individualCharacterIterationTest(){
        System.out.println("Individual character test");
        File simpleAverageResultFile = new File(resultPath + "simpleAverageIndividualCharacterIterationTest" + System.currentTimeMillis() + fileEnding);
        File priorityAverageResultFile = new File(resultPath + "priorityAverageIndividualCharacterIterationTest" + System.currentTimeMillis() + fileEnding);
        int numberOfIterations = 7;
        int numberOfQueries = 1000;
        int numberOfEdits = 2;

        try {
            BufferedWriter simpleWriter = new BufferedWriter(new FileWriter(simpleAverageResultFile));
            BufferedWriter priorityWriter = new BufferedWriter(new FileWriter(priorityAverageResultFile));

            int numberOfRecivedIndexTerms = 0;
            while(numberOfRecivedIndexTerms < numberOfQueries){
                String candidateTerm = index.getRandomIndexTerm();
                String modifiedTerm = TermModifier.modifyTerm(numberOfEdits, candidateTerm);
                if(modifiedTerm.length() >= numberOfIterations){
                    numberOfRecivedIndexTerms++;

                    searchHandler = new InteractiveSearchHandler(index, 10, false, numberOfEdits);
                    performIterativeSearchWithBookkeping(numberOfIterations, simpleAverageResultFile, modifiedTerm, simpleWriter);
                    searchHandler = new InteractiveSearchHandler(index, 10, true, numberOfEdits);
                    performIterativeSearchWithBookkeping(numberOfIterations, priorityAverageResultFile, modifiedTerm, priorityWriter);
                }
            }

            simpleWriter.close();
            priorityWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void performIterativeSearchWithBookkeping(
            int largestTerm,
            File resultFile,
            String testQuery,
            BufferedWriter resultWriter) throws IOException
    {
        ArrayList<Long> results = new ArrayList<Long>();
        for(int i = 0; i < largestTerm; i++){
            String queryString = testQuery.substring(0, i);
            long startTime = System.nanoTime();
            searchHandler.handleUserInput(queryString);
            long endTime = System.nanoTime();
            long timeUsage = endTime - startTime;
            results.add(timeUsage);
        }

        StringBuilder resultRecord = new StringBuilder("");

        for(int i = 0; i < results.size(); i++){
            resultRecord.append(results.get(i));
            if(!(i == results.size() - 1)){
                resultRecord.append("; ");
            }
        }

        resultRecord.append("\n");
        resultWriter.append(resultRecord.toString());
    }

    private long doInteractiveSearch(String term){
        long totalTime = 0;
        for(int i = 1; i <= term.length(); i++){
            String queryString = term.substring(0, i);
            long startTime = System.nanoTime();
            searchHandler.handleUserInput(queryString);
            long endTime = System.nanoTime();
            totalTime += endTime - startTime;
        }

        return totalTime;
    }

    public static void main(String[] args){
        PerformanceTest performanceTest = new PerformanceTest();
        //performanceTest.individualCharacterIterationTest();
        performanceTest.editDistanceScalingTest();
        performanceTest.randomCharacterTest();
    }
}
