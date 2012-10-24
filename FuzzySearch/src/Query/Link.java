package Query;

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

    public String toString(){
        return "Rank: " + rank;
    }

    public boolean isValid(QueryString queryString){
        return true;
    }
}
