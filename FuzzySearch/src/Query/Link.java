package Query;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        10.10.12
 * Time:        12:24
 * To change this template use File | Settings | File Templates.
 */
public abstract class Link implements Comparable<Link> {
    private double rank;

    public Link(double rank){

        this.rank = rank;
    }

    public double getRank(){
        return rank;
    }

    public abstract ActivePriorityNode UseLink();

    public int compareTo(Link link){
        double difference = link.rank - this.rank;
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
