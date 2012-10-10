import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        30.09.12
 * Time:        09:50
 * To change this template use File | Settings | File Templates.
 */
public class SearchPanel extends JPanel {

    JTextField searchField = new JTextField(50);
    private InteractiveSearchHandler interactiveSearchHandler;
    JTextArea resultArea = new JTextArea(10, 50);
    private final int searchLimit = 10;

    public SearchPanel(InteractiveSearchHandler interactiveSearchHandler){
        this.interactiveSearchHandler = interactiveSearchHandler;
        searchField.setEditable(true);
        searchField.addCaretListener( new CaretListener() {
            public void caretUpdate(CaretEvent e) {
                String queryString = searchField.getText();
                handleUserInput(queryString);
            }
        });
        searchField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                searchField.setText("");
                handleUserInput("");
            }
        });

        add(searchField);
        resultArea.setEditable(false);
        add(resultArea);
    }

    private void handleUserInput(String queryString){
        interactiveSearchHandler.handleUserInput(queryString);
        ArrayList<String> results = interactiveSearchHandler.getSearchResults();
        resultArea.setText("");
        for(int i = 0; i < Math.min(results.size(), searchLimit); i++){
            resultArea.append(results.get(i) + "\n");
        }
    }
}
