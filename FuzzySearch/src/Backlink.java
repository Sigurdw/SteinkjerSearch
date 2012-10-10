/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        10.10.12
 * Time:        12:24
 * To change this template use File | Settings | File Templates.
 */
public class Backlink implements Comparable<Backlink> {
    private double rank;
    private BestFirstActiveQuery activeQuery;

    public Backlink(double rank, BestFirstActiveQuery bestFirstactiveQuery){

        this.rank = rank;
        this.activeQuery = bestFirstactiveQuery;
    }

    public double getRank(){
        return rank;
    }

    public BestFirstActiveQuery getActiveQuery() {
        return activeQuery;
    }

    public int compareTo(Backlink backlink){
        double difference = backlink.rank - this.rank;
        if(difference > 0){
            return 1;
        }
        else if(difference < 0){
            return -1;
        }
        else{
            return 0;
        }
    }
}
