package Query;

public class BackLink extends Link{
    ActivePriorityNode destination;

    public BackLink(double rank,  ActivePriorityNode destination) {
        super(rank);
        this.destination = destination;
    }

    @Override
    public ActivePriorityNode UseLink() {
        System.out.println("Using back link: " + this);
        return destination;
    }

    public String toString(){
        return "BackLink to " + destination.getLabel();
    }
}
