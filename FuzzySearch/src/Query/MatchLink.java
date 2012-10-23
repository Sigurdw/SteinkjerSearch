package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;

public class MatchLink extends Link{
    ActivePriorityNode sourceNode;
    Trie<IDocument> position;

    public MatchLink(double rank, ActivePriorityNode sourceNode, Trie<IDocument> position) {
        super(rank);
        this.sourceNode = sourceNode;
        this.position = position;
    }

    @Override
    public ActivePriorityNode UseLink() {
        System.out.print("Using match link: " + this);
        return sourceNode.createChild(position, EditOperation.Match);
    }

    public String toString(){
        return "Match from " + sourceNode.getLabel() + " to " + position.getLabel() + " " + super.toString();
    }
}
