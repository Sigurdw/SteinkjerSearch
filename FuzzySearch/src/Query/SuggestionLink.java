package Query;

public class SuggestionLink extends Link{

    private final double rank;
    private final int validIterationId;
    private final ActivePriorityNode source;
    private final ActivePriorityNode destinationNode;

    public SuggestionLink(double rank, int validIterationId, ActivePriorityNode source, ActivePriorityNode destinationNode) {
        super(rank);
        this.rank = rank;
        this.validIterationId = validIterationId;
        this.source = source;
        this.destinationNode = destinationNode;
    }

    @Override
    public ActivePriorityNode UseLink() {
        System.out.println("Using suggestion link: " + this);
        BackLink backLink = source.makeBackLink(destinationNode);
        System.out.println(backLink);
        destinationNode.addLink(backLink);
        return destinationNode;
    }

    public boolean isValid(QueryString queryString){
        return queryString.GetLength() == validIterationId;
    }

    public String toString(){
        return "SuggestionLink from "
                + source.getLabel()
                + " to " + destinationNode.getLabel()
                + " " + super.toString();
    }
}
