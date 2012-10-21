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
    private ActivePriorityNode destinationNode;

    public ForwardLink(double rank, ActivePriorityNode destinationNode) {
        super(rank);
        this.destinationNode = destinationNode;
    }

    @Override
    public ActivePriorityNode UseLink() {
        System.out.println("Using forward link: " + this);
        return destinationNode;
    }

    public String toString(){
        return "ForwardLink to " + destinationNode.getLabel();
    }
}
