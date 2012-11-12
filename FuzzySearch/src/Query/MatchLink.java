package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;

public class MatchLink extends Link{
    ActivePriorityNode sourceNode;
    Trie<IDocument> position;
    private int numberOfEdits;
    private int queryStringIndex;
    private double editDiscount;

    public MatchLink(
            double rank,
            ActivePriorityNode sourceNode,
            Trie<IDocument> position,
            int numberOfEdits,
            int queryStringIndex,
            double editDiscount) {
        super(rank);
        this.sourceNode = sourceNode;
        this.position = position;
        this.numberOfEdits = numberOfEdits;
        this.queryStringIndex = queryStringIndex;
        this.editDiscount = editDiscount;
    }

    @Override
    public ActivePriorityNode UseLink() {
        //System.out.print("Using match link: " + this);
        return sourceNode.createChild(
                position,
                EditOperation.Match,
                numberOfEdits,
                queryStringIndex,
                editDiscount,
                false);
    }

    public String toString(){
        return "Match from " + sourceNode.getLabel() + " to " + position.getLabel() + " " + super.toString();
    }
}
