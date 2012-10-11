import sun.font.TrueTypeFont;

import javax.transaction.TransactionRolledbackException;
import java.util.*;

public class Trie<T> implements Comparable<Trie<T>> {
    private ArrayList<T> dataList = new ArrayList<T>();
    private Map<Character, Trie<T>> children = new HashMap<Character, Trie<T>>();
    private ArrayList<Trie<T>> rankSortedChildren = new ArrayList<Trie<T>>();
    private String label;
    private PriorityQueue<Trie<T>> suggestionCache = new PriorityQueue<Trie<T>>();
    private int termFrequency = 0;

    private double rank;

    public Trie(){
        this.label = "";
    }

    public Trie(String label){
        this.label = label;
    }

    public void addKeyDataPair(String key, T data){
        addKeyDataPair(key, data, 0);
    }

    private Trie<T> addKeyDataPair(String key, T data, int depth){
        Trie<T> addedTrie;

        if((key.length() - depth) == 0){
            if(!dataList.contains(data)){
                dataList.add(data);
            }

            termFrequency++;
            rank = Math.max((double)termFrequency / (double)dataList.size(), rank);

            addedTrie = this;
        }
        else{
            char childKey = key.charAt(depth);
            Trie<T> child;
            if(children.containsKey(childKey)){
                child = children.get(childKey);
                addedTrie = child.addKeyDataPair(key, data, depth + 1);
            }
            else{
                child = new Trie<T>(label + childKey);
                addedTrie = child.addKeyDataPair(key, data, depth + 1);
                children.put(childKey, child);
                rankSortedChildren.add(child);
            }

            rank = Math.max(child.getRank(), rank);
            //This should be changed (too expensice):
            Collections.sort(rankSortedChildren);
        }

        maybeAddTrieToCache(addedTrie);

        return addedTrie;
    }

    public Trie<T> getOrderedChild(int index){
        System.out.println("List of ranks:");
        System.out.println(rankSortedChildren);
        System.out.println();
        return rankSortedChildren.get(index);
    }

    public int getSize(){
        return rankSortedChildren.size();
    }

    private void maybeAddTrieToCache(Trie<T> addedTrie) {
        suggestionCache.add(addedTrie);
    }

    public ArrayList<T> search(String key ){
        return search(key, 0);
    }

    public PriorityQueue<Trie<T>> getCachedSuggestions(){
        return suggestionCache;
    }

    public ArrayList<String> getSuggestions(String partialKey){
        return getSuggestions(partialKey, 0);
    }

    private ArrayList<String> getSuggestions(String partialKey, int depth){
        ArrayList<String> suggestions;
        if(partialKey.length() - depth == 0){
            suggestions = new ArrayList<String>();
            getLabelsRecursive(suggestions);
        }
        else if(children.containsKey(partialKey.charAt(depth))){
            suggestions = children.get(partialKey.charAt(depth)).getSuggestions(partialKey, depth + 1);
        }
        else{
            suggestions = new ArrayList<String>();
        }

        return suggestions;
    }

    public void getLabelsRecursive(ArrayList<String> labelList){
        if(!dataList.isEmpty()){
            labelList.add(label);
        }

        for(Trie<T> child : children.values()){
            child.getLabelsRecursive(labelList);
        }
    }

    public Map<Character, Trie<T>> getChildren(){
        return children;
    }

    private ArrayList<T> search(String key, int depth){
        ArrayList<T> results;
        if((key.length() - depth) == 0){
            results = dataList;
        }
        else{
            char childKey = key.charAt(depth);
            if(children.containsKey(childKey)){
                results = children.get(childKey).search(key, depth + 1);
            }
            else{
                results = new  ArrayList<T>();
            }
        }

        return results;
    }

    public ArrayList<T> getData() {
        return dataList;
    }

    public double getRank(){
        return rank;
    }

    public int getNumberOfEntries(){
        int numberOfEntries = dataList.size();
        for(Trie<T> child : children.values()){
            numberOfEntries += child.getNumberOfEntries();
        }

        return numberOfEntries;
    }

    public int compareTo(Trie<T> trie){
        double difference = trie.getRank() - getRank();
        if(difference == 0){
            return 0;
        }
        else if(difference > 0){
            return 1;
        }
        else{
            return -1;
        }
    }

    public String getLabel() {
        return label;
    }

    public String toString(){
        return label + ", " + rank;
    }
}
