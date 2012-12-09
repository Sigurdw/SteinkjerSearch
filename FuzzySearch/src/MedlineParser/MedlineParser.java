package MedlineParser;

import java.io.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class MedlineParser {

    private static final String Path = "D:/medline2004.txt";

    private static final String Dochead = "#NEW RECORDPMID";

    private static final String DocPrefix = "Medline";

    public static void main(String[] args) {
        try {
            File file = new File(Path);
            Scanner scanner = new Scanner(file);
            scanner.useDelimiter("#NEW RECORD\n");
            int counter = 0;
            while (scanner.hasNext()){

                String document = scanner.next();

                if(!document.equals("\n")){
                    StringTokenizer st = new StringTokenizer(document, "\n");
                    assert st.countTokens() == 5;

                    String pmid = st.nextToken();
                    String pubdate = st.nextToken();
                    String title = st.nextToken().substring(8);
                    String text = st.nextToken().substring(11);
                    while(title.endsWith(".")){
                        title = title.substring(0, title.length() - 1);
                    }

                    BufferedWriter bf = null;

                    try{
                        String fileName = "C:/TextCollection/" + DocPrefix + counter + ".txt";
                        File newFile = new File(fileName);
                        System.out.println(fileName);
                        bf = new BufferedWriter(new FileWriter(newFile));
                        bf.write(title + "\n" + text);
                        bf.close();
                        counter++;
                    }
                    catch(FileNotFoundException e){
                        e.printStackTrace();
                    }
                    catch (IOException e){
                        e.printStackTrace();
                    }

                    finally {
                        if(bf != null){
                            bf.close();
                        }

                    }


                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            System.out.println("File not found.");
            System.exit(1);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Error in document processing.");
            System.exit(1);
        }
    }
}
