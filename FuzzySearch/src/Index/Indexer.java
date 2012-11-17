package Index;

import DataStructure.Trie;
import DocumentModel.IDocument;

import java.util.ArrayList;
import java.util.List;

public class Indexer {

    public static int NumberOfDocuments = 0;
    private int suggestionCacheSize;
    private boolean hasSortedIndex;

    public Indexer(int suggestionCacheSize, boolean hasSortedIndex){
        this.suggestionCacheSize = suggestionCacheSize;
        this.hasSortedIndex = hasSortedIndex;
    }

    public Index indexDocuments(ArrayList<IDocument> documents){
        NumberOfDocuments = documents.size();
        Trie<IDocument> indexImplementation = new Trie<IDocument>(suggestionCacheSize, hasSortedIndex);
        for(IDocument document : documents){
            List<String> tokens = document.getTokens();
            List<String> indexTerms = getIndexTerms(tokens);
            for(String indexTerm : indexTerms){
                indexImplementation.addKeyDataPair(indexTerm, document);
            }
        }

        indexImplementation.sortData();
        System.out.println(
                "Successfully indexed " + indexImplementation.getNumberOfEntries() + " different index terms.");
        return new Index(indexImplementation);
    }

    private List<String> getIndexTerms(List<String> tokens) {
        ArrayList<String> indexTerms = new ArrayList<String>();
        for(String token : tokens){
            String indexTerm = token.toLowerCase();
            indexTerm = removeSigns(indexTerm);

            if(indexTerm.length() > 0){
                indexTerms.add(indexTerm);
            }
        }

        return indexTerms;
    }

    private String removeSigns(String indexTerm) {
        char lastCharacter = indexTerm.charAt(indexTerm.length() - 1);
        if(!Character.isLetterOrDigit(lastCharacter)){
            return indexTerm.substring(0, indexTerm.length() - 1);
        }

        return indexTerm;
    }
}
