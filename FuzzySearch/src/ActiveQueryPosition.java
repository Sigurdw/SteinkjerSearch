/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        30.09.12
 * Time:        13:00
 * To change this template use File | Settings | File Templates.
 */
public class ActiveQueryPosition implements Comparable<ActiveQueryPosition> {

    private ActiveQuery activeQuery;
    private int position;
    private double rank;

    public ActiveQueryPosition(ActiveQuery activeQuery, int position, double rank){

        this.activeQuery = activeQuery;
        this.position = position;
        this.rank = rank;
    }

    public ActiveQuery getActiveQuery() {
        return activeQuery;
    }

    public int getPosition() {
        return position;
    }

    public double getRank() {
        return rank;
    }

    @Override
    public int compareTo(ActiveQueryPosition otherPosition) {
        double rankDifference = otherPosition.getRank() - getRank();
        if(rankDifference > 0){
            return 1;
        }
        else if(rankDifference < 0){
            return -1;
        }
        else{
            return 0;
        }
    }
}
