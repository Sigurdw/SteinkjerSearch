package DocumentModel;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created with IntelliJ IDEA.
 * User: Sigurd
 * Date: 23.09.12
 * Time: 19:17
 * To change this template use File | Settings | File Templates.
 */
public class Document implements IDocument {
    private File documentFile;

    public Document(File documentFile){
        this.documentFile = documentFile;
    }

    public String getName() {
        return documentFile.getName();
    }

    public String getPath() {
        return documentFile.getPath();
    }

    public List<String> getTokens() {
        ArrayList<String> documentTokens = new ArrayList<String>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(documentFile));
            extractTokensFromDocument(documentTokens, bufferedReader);
            bufferedReader.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found.");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in document processing.");
            System.exit(1);
        }

        return documentTokens;
    }

    private void extractTokensFromDocument(
            ArrayList<String> documentTokens,
            BufferedReader bufferedReader)
            throws IOException {
        String line;
        while((line = bufferedReader.readLine()) != null){
            StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
            while(stringTokenizer.hasMoreTokens()){
                String token = stringTokenizer.nextToken();
                documentTokens.add(token);
            }
        }
    }

    public String toString(){
        return "DocumentModel.Document: " + getName() + " Path: " + getPath();
    }
}
