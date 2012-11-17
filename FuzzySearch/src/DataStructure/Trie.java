package DataStructure;

import java.util.*;

public class Trie<T> implements Comparable<Trie<T>> {
    private ArrayList<T> dataList = new ArrayList<T>();
    private Map<Character, Trie<T>> children = new HashMap<Character, Trie<T>>();
    private ArrayList<Trie<T>> rankSortedChildren;
    private String label;
    private ArrayList<Trie<T>> suggestionCache = new ArrayList<Trie<T>>();
    private int cacheSize;
    private boolean hasSortedIndex;
    private int termFrequency = 0;

    private double rank;

    public Trie(int cacheSize, boolean hasSortedIndex){
        this("", cacheSize, hasSortedIndex);
    }

    private Trie(String label, int cacheSize, boolean hasSortedIndex){
        this.label = label;
        this.cacheSize = cacheSize;
        this.hasSortedIndex = hasSortedIndex;
        if(hasSortedIndex){
            rankSortedChildren = new ArrayList<Trie<T>>();
        }
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
            rank = (double)termFrequency / (double)dataList.size();

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
                child = new Trie<T>(label + childKey, cacheSize, hasSortedIndex);
                addedTrie = child.addKeyDataPair(key, data, depth + 1);
                children.put(childKey, child);
                if(hasSortedIndex){
                    rankSortedChildren.add(child);
                }
            }

            rank = Math.max(rank, child.getRank()); // = rankSortedChildren.get(0).getRank();
        }

        maybeAddTrieToCache(addedTrie);

        return addedTrie;
    }

    public void sortData(){
        if(hasSortedIndex){
            Collections.sort(rankSortedChildren);
            for(Trie<T> child : children.values()){
                child.sortData();
            }
        }
    }

    public Trie<T> getOrderedChild(int index){
        return rankSortedChildren.get(index);
    }

    public int getSize(){
        return rankSortedChildren.size();
    }

    private void maybeAddTrieToCache(Trie<T> addedTrie) {
        if(!suggestionCache.contains(addedTrie)){
            suggestionCache.add(addedTrie);
        }

        Collections.sort(suggestionCache);

        if(suggestionCache.size() > cacheSize){
            suggestionCache.remove(cacheSize);
        }
    }

    public ArrayList<T> search(String key ){
        return search(key, 0);
    }

    public ArrayList<Trie<T>> getCachedSuggestions(){
        return suggestionCache;
    }

    public ArrayList<String> getSuggestions(){
        ArrayList<String> suggestions = new ArrayList<String>();
        getSuggestions(suggestions);
        return suggestions;
    }

    private void getSuggestions(ArrayList<String> suggestions){
        if(dataList.size() > 0){
            suggestions.add(label);
        }

        for(Trie<T> child :children.values()){
            child.getSuggestions(suggestions);
        }
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
