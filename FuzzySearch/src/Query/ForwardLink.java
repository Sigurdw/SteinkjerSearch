package Query;

public class ForwardLink extends Link{
    private ActivePriorityNode source;
    private ActivePriorityNode destinationNode;

    public ForwardLink(double rank, ActivePriorityNode source, ActivePriorityNode destinationNode) {
        super(rank);
        this.source = source;
        this.destinationNode = destinationNode;
    }

    @Override
    public ActivePriorityNode UseLink() {
        //System.out.println("Using forward link: " + this);
        BackLink backLink = source.makeBackLink(destinationNode);
        destinationNode.addLink(backLink);
        return destinationNode;
    }

    public String toString(){
        return "ForwardLink from "
                + source.getLabel()
                + " to " + destinationNode.getLabel()
                + " " + super.toString();
    }
}
