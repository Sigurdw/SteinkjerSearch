package Interface;

import DocumentModel.Document;
import DocumentModel.IDocument;
import Index.Index;
import Index.Indexer;
import Index.Crawler;
import Query.InteractiveSearchHandler;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        30.09.12
 * Time:        09:42
 * To change this template use File | Settings | File Templates.
 */
public class MainFrame extends JFrame {

    private static final int width = 800;
    private static final int height = 600;
    private static final String directoryPath = "D:/TextCollection";

    public MainFrame(){
        setSize(width, height);
        Crawler crawler = new Crawler(directoryPath);
        ArrayList<IDocument> documents = crawler.crawlDirectory();
        Indexer indexer = new Indexer();
        Index index = indexer.indexDocuments(documents);
        //ArrayList<DocumentModel.Document> result = index.search("the");
        InteractiveSearchHandler interactiveSearchHandler = new InteractiveSearchHandler(index);
        SearchPanel searchPanel = new SearchPanel(interactiveSearchHandler);
        add(searchPanel);
        setTitle("Fuzzy Search");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
    }
}
