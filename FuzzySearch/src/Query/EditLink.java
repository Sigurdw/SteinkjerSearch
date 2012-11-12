package Query;

import DataStructure.Trie;
import DocumentModel.IDocument;

public class EditLink extends Link implements IDiscardableLink {
    Trie<IDocument> position;
    ActivePriorityNode sourceNode;
    ActivePriorityNode newSourceNode = null;
    EditOperation editOperation;
    private int numberOfEdits;
    private int queryStringIndex;
    private double editDiscount;
    private boolean substitution;

    public EditLink(
            double rank,
            ActivePriorityNode sourceNode,
            Trie<IDocument> position,
            EditOperation editOperation,
            int numberOfEdits,
            int queryStringIndex,
            double editDiscount,
            boolean isSubstitution)
    {
        super(rank);
        this.position = position;
        this.sourceNode = sourceNode;
        this.editOperation = editOperation;
        this.numberOfEdits = numberOfEdits;
        this.queryStringIndex = queryStringIndex;
        this.editDiscount = editDiscount;
        substitution = isSubstitution;
    }

    @Override
    public ActivePriorityNode UseLink() {
        //System.out.println("Using " + this);
        if(newSourceNode != null){
            if(editOperation == EditOperation.Insert){
                EditLink insertLink = sourceNode.stealNextEditLink();
                if(insertLink != null){
                    newSourceNode.addLink(insertLink);
                }
            }

            return newSourceNode.createChild(
                    position,
                    editOperation,
                    numberOfEdits,
                    queryStringIndex,
                    editDiscount,
                    substitution);
        }
        else{
            sourceNode.maybyAddNextActiveNode(editOperation);
            return sourceNode.createChild(
                    position,
                    editOperation,
                    numberOfEdits,
                    queryStringIndex,
                    editDiscount,
                    substitution);
        }
    }

    public String toString(){
        return editOperation +
                " link from " +
                sourceNode.getLabel() +
                " to " + position.getLabel() +
                " " + super.toString();
    }

    @Override
    public void setSource(ActivePriorityNode source) {
        newSourceNode = source;
    }

    @Override
    public boolean isValid(QueryString queryString) {
        return true; //return (double)numberOfEdits / (double)queryString.GetLength() < 0.75;
    }
}
