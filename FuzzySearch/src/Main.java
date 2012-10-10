import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        23.09.12
 * Time:        18:48
 * To change this template use File | Settings | File Templates.
 */
public class Main {
    private static final String directoryPath = "C:/TextCollection";


    public static void main(String args[]){
        /*Crawler crawler = new Crawler(directoryPath);
        ArrayList<Document> documents = crawler.crawlDirectory();
        Indexer indexer = new Indexer();
        Index index = indexer.indexDocuments(documents);
        ArrayList<Document> result = index.search("the");
        printResult(result);

        result = index.search("protein");
        printResult(result);

        result = index.search("thisShouldNotYieldResults");
        printResult(result);

        ArrayList<String> suggestions = index.getSuggestions("");
        printSuggestions(suggestions);

        InteractiveSearchHandler interactiveSearchHandler = new InteractiveSearchHandler(index);
        interactiveSearchHandler.addCharacter('r');
        interactiveSearchHandler.addCharacter('h');
        interactiveSearchHandler.addCharacter('o');
        interactiveSearchHandler.addCharacter('t');
        interactiveSearchHandler.addCharacter('a');
        interactiveSearchHandler.addCharacter('j');
        interactiveSearchHandler.addCharacter('n');*/
        MainFrame frame = new MainFrame();
    }

    public static void printResult(ArrayList<Document> documents){
        System.out.println("The query had " + documents.size() + " hits");
        for(Document document : documents){
            System.out.println(document);
        }
    }

    public static void printSuggestions(ArrayList<String> suggestions){
        System.out.println("The query had " + suggestions.size() + " suggestions");
        for(String suggestion : suggestions){
            System.out.println(suggestion);
        }
    }
}
