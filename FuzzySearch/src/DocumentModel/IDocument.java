package DocumentModel;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        21.10.12
 * Time:        12:41
 * To change this template use File | Settings | File Templates.
 */
public interface IDocument {
    public String getName();

    public String getPath();

    public List<String> getTokens();
}
