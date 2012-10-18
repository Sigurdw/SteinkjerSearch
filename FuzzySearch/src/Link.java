/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        10.10.12
 * Time:        12:24
 * To change this template use File | Settings | File Templates.
 */
public class Link implements Comparable<Link> {
    private double rank;
    private ActivePriorityNode activePriorityNode;
    private boolean isShortcut = false;

    public Link(double rank, ActivePriorityNode bestFirstactivePriorityNode){

        this.rank = rank;
        this.activePriorityNode = bestFirstactivePriorityNode;
    }

    public Link(double rank, ActivePriorityNode bestFirstactivePriorityNode, boolean isShortcut){
        this.rank = rank;
        this.activePriorityNode = bestFirstactivePriorityNode;
        this.isShortcut = isShortcut;
    }

    public double getRank(){
        return rank;
    }

    public ActivePriorityNode getActivePriorityNode() {
        return activePriorityNode;
    }

    public int compareTo(Link backlink){
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

    public boolean isShortCut(){
        return isShortcut;
    }
}
