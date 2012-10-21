package Tests;

import DocumentModel.IDocument;

import java.util.List;

public class TestDocument implements IDocument {

    private String name;
    private final String path;
    private final List<String> tokens;

    public TestDocument(String name, String path, List<String> tokens){

        this.name = name;
        this.path = path;
        this.tokens = tokens;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public List<String> getTokens() {
        return tokens;
    }
}
