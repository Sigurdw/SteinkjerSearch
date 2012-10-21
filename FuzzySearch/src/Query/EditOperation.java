package Query;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        28.09.12
 * Time:        17:25
 * To change this template use File | Settings | File Templates.
 */
public enum EditOperation {
    Insert,
    Delete,
    Substitution,
    Match;

    public static int getOperationCost(EditOperation editOperation){
        int cost = -1;

        switch (editOperation){
            case Insert:
                cost = 1;
                break;
            case Delete:
                cost = 1;
                break;
            case Match:
                cost = 0;
                break;
            case Substitution:
                cost = 1;
                break;
        }

        return cost;
    }

    public static int getOperationMovement(EditOperation editOperation){
        int movement = -1;

        switch (editOperation){
            case Insert:
                movement = 1;
                break;
            case Delete:
                movement = 0;
                break;
            case Match:
                movement = 1;
                break;
            case Substitution:
                movement = 1;
                break;
        }

        return movement;
    }
}
