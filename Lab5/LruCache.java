package javalabs.Lab5;

import java.util.LinkedHashMap;
import java.util.Map;

public class LruCache<K, V> {
    private final int capacity;
    private final LinkedHashMap<K, V> map;

    public LruCache(int capacity) {
        if (capacity <= 0) throw new IllegalArgumentException("capacity <= 0");
        this.capacity = capacity;
        this.map = new LinkedHashMap<K, V>(16, 0.75f, true) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
                return size() > LruCache.this.capacity;
            }
        };
    }

    public V get(K key) {
        if (key == null) throw new IllegalArgumentException("key == null");
        return map.get(key);  
    }

    public void put(K key, V value) {
        if (key == null) throw new IllegalArgumentException("key == null");
        if (value == null) throw new IllegalArgumentException("value == null");
        map.put(key, value);
    }

    public boolean containsKey(K key) {
        if (key == null) throw new IllegalArgumentException("key == null");
        return map.containsKey(key);
    }

    public int size() {
        return map.size();
    }

    public int capacity() {
        return capacity;
    }

    @Override
    public String toString() {
        return map.toString();
    }
    public static void main(String[] args) {
        LruCache<Integer, String> cache = new LruCache<Integer, String>(3);

        cache.put(1, "A");
        cache.put(2, "B");
        cache.put(3, "C");
        System.out.println("После 3 put:      " + cache);   
        cache.get(1);                                        
        System.out.println("После get(1):     " + cache);   

        cache.put(4, "D");                                  
        System.out.println("После put(4,D):   " + cache);   

        System.out.println("get(2) -> " + cache.get(2));    
        System.out.println("get(1) -> " + cache.get(1));    
        System.out.println("size = " + cache.size());       
    }
}