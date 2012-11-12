package Query;

public class ShortcutLink extends Link {
    private ActivePriorityNode source;
    private ActivePriorityNode destinationNode;

    public ShortcutLink(double rank, ActivePriorityNode source, ActivePriorityNode destinationNode) {
        super(rank);
        this.source = source;
        this.destinationNode = destinationNode;
    }

    @Override
    public ActivePriorityNode UseLink() {
        //System.out.println("Using shortcut link: " + this);
        ShortcutLink backLink = source.makeShortcutLink(destinationNode);
        destinationNode.addLink(backLink);
        return destinationNode;
    }

    public String toString(){
        return "ShortcutLink from " + source.getLabel() + " to " + destinationNode.getLabel() + " " + super.toString();
    }
}
