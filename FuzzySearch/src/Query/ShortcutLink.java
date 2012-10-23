package Query;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        21.10.12
 * Time:        17:24
 * To change this template use File | Settings | File Templates.
 */
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
        System.out.println("Using shortcut link: " + this);
        ShortcutLink backLink = source.makeShortcutLink(destinationNode);
        destinationNode.addLink(backLink);
        return destinationNode;
    }

    public String toString(){
        return "ShortcutLink from " + source.getLabel() + " to " + destinationNode.getLabel() + " " + super.toString();
    }
}
