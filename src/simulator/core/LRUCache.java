package simulator.core;

import java.util.LinkedHashMap;
import java.util.Map.Entry;
 
public class LRUCache < K, V > extends LinkedHashMap < K, V > {

	private static final long serialVersionUID = 1L;
	private int capacity; // Maximum number of items in the cache.
     
    public LRUCache(int capacity) { 
        super(capacity+1, 1.0f, true); // Pass 'true' for accessOrder.
        this.capacity = capacity;
    }
     
    protected boolean removeEldestEntry(@SuppressWarnings("rawtypes") Entry entry) {
        return (size() > this.capacity);
    } 
}