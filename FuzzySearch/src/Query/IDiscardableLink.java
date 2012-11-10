package Query;

public interface IDiscardableLink {
    public void setSource(ActivePriorityNode source);

    public boolean isValid(QueryString queryString);
}
