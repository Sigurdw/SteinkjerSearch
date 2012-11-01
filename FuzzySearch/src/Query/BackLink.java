package Query;

public class BackLink extends Link{
    private ActivePriorityNode source;
    private ActivePriorityNode destination;

    public BackLink(double rank, ActivePriorityNode destination) {
        super(rank);
        this.destination = destination;
    }

    public BackLink(double rank, ActivePriorityNode source, ActivePriorityNode destination) {
        super(rank);
        this.source = source;
        this.destination = destination;
    }

    public void setSource(ActivePriorityNode source){
        this.source = source;
    }

    @Override
    public ActivePriorityNode UseLink() {
        System.out.println("Using back link: " + this);
        Link forwardLink;
        if(source.isExhausted()){
            forwardLink = source.makeSuggestionLink(destination);
        }
        else {
            forwardLink = source.makeForwardLink(destination);
        }

        System.out.println("Making forward link: " + forwardLink.toString());
        destination.addLink(forwardLink);
        return destination;
    }

    public String toString(){
        return "BackLink from " + source.getLabel() + " to " + destination.getLabel() + " " + super.toString();
    }
}
