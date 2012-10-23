package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;

public class EditLink extends Link {
    Trie<IDocument> position;
    ActivePriorityNode souceNode;
    EditOperation editOperation;

    public EditLink(double rank, ActivePriorityNode sourceNode, Trie<IDocument> position, EditOperation editOperation) {
        super(rank);
        this.position = position;
        this.souceNode = sourceNode;
        this.editOperation = editOperation;
    }

    @Override
    public ActivePriorityNode UseLink() {
        System.out.println("Using " + this);
        return souceNode.createChild(position, editOperation);
    }

    public String toString(){
        return editOperation +
                " link from " +
                souceNode.getLabel() +
                " to " + position.getLabel() +
                " " + super.toString();
    }
}
