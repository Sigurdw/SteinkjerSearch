package Tests;

import Query.EditOperation;
import org.junit.Test;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        22.10.12
 * Time:        17:53
 * To change this template use File | Settings | File Templates.
 */
public class EditOperationTest {
    @Test
    public void testIsOperationAllowed() throws Exception {
        assert !EditOperation.isOperationAllowed(EditOperation.Match, EditOperation.Substitution);
        assert !EditOperation.isOperationAllowed(EditOperation.Delete, EditOperation.Insert);
        assert EditOperation.isOperationAllowed(EditOperation.Match, EditOperation.Match);
        assert EditOperation.isOperationAllowed(EditOperation.Delete, EditOperation.Delete);
        assert EditOperation.isOperationAllowed(EditOperation.Insert, EditOperation.Insert);
        assert EditOperation.isOperationAllowed(EditOperation.Insert, EditOperation.Delete);
    }

    @Test
    public void testGetOperationCost() throws Exception {

    }

    @Test
    public void testGetOperationMovement() throws Exception {

    }
}
