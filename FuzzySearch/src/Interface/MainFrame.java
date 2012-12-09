package Interface;

import DocumentModel.IDocument;
import Index.Index;
import Index.Indexer;
import Index.Crawler;
import Query.InteractiveSearchHandler;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    private static final int width = 800;
    private static final int height = 600;
    private static final String directoryPath = "C:/TextCollection";
    private static final int numberOfSuggestionsRequired = 10;
    private static final boolean usePrioritySearch = true;
    private static final int allowedEditDistance = 3;

    public MainFrame(){
        setSize(width, height);
        InteractiveSearchHandler interactiveSearchHandler = setUpSearchEngine();
        SearchPanel searchPanel = new SearchPanel(interactiveSearchHandler);
        add(searchPanel);
        setTitle("Fuzzy Search");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }

    private InteractiveSearchHandler setUpSearchEngine() {
        //Crawler crawler = new Crawler(directoryPath);
        //ArrayList<IDocument> documents = crawler.crawlDirectory();
        Indexer indexer = new Indexer(numberOfSuggestionsRequired, usePrioritySearch);
        Index index = null;
        try {
            index = indexer.indexDocumentBlob(new File(directoryPath + "/medline2004.txt"), 100000);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        return new InteractiveSearchHandler(index, numberOfSuggestionsRequired, usePrioritySearch, allowedEditDistance);
    }
}
