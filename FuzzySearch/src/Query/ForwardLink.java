package Query;

import com.sun.xml.internal.ws.wsdl.writer.UsingAddressing;

/**
 * Created with IntelliJ IDEA.
 * Copywrite:   Sigurd Wien
 * User:        Sigurd
 * Date:        21.10.12
 * Time:        16:05
 * To change this template use File | Settings | File Templates.
 */
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
        System.out.println("Using forward link: " + this);
        BackLink backLink = source.makeBackLink(destinationNode);
        destinationNode.addLink(backLink);
        return destinationNode;
    }

    public String toString(){
        return "ForwardLink from " + source.getLabel() + " to " + destinationNode.getLabel();
    }
}
