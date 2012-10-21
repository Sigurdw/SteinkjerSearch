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
    private ActivePriorityNode destinationNode;

    public ShortcutLink(double rank, ActivePriorityNode destinationNode) {
        super(rank);
        this.destinationNode = destinationNode;
    }

    @Override
    public ActivePriorityNode UseLink() {
        System.out.println("Using shortcut link: " + this);
        return destinationNode;
    }

    public String toString(){
        return "ShortcutLink to " + destinationNode.getLabel();
    }
}
