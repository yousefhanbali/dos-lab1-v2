package org.example;

import java.util.Collection;
import java.util.HashMap;
import java.util.Set;

class CacheEntry<K, V>{
    public long lastAccessed;
    public V value;
    public K key;
    public CacheEntry(K key, V value){
        this.key = key;
        this.value = value;
        lastAccessed = System.currentTimeMillis(); // Never accessed
    }
}

public class LRUCache<V>{
    private final HashMap<String, CacheEntry<String, V>> _cacheMap;
    private int _maximumNumberOfItems = 10;
    public LRUCache(int maximumNumberOfItems){
        _cacheMap = new HashMap<>();
        _maximumNumberOfItems = maximumNumberOfItems;
    }
    public void invalidate(String key){
        _cacheMap.remove(key);
        Set<String> keys = _cacheMap.keySet();
        for(String entry: keys){
            if(entry.contains("search")){
                _cacheMap.remove(entry);
            }
        }
    }
    public void store(String key, V value){
        if(!_cacheMap.containsKey(key)){
            if(_cacheMap.size() >= _maximumNumberOfItems){
                Collection<CacheEntry<String, V>> values = _cacheMap.values();
                CacheEntry<String, V> oldestEntry = null;
                for(CacheEntry<String, V> entry: values){
                    if(oldestEntry == null || entry.lastAccessed < oldestEntry.lastAccessed){
                        oldestEntry = entry;
                    }
                }
                if(oldestEntry != null){
                    _cacheMap.remove(oldestEntry.key);
                }
            }
            CacheEntry<String, V> entry = new CacheEntry<String, V>(key, value);
            _cacheMap.put(key, entry);
        }
    }
    public V get(String key){
        if(!_cacheMap.containsKey(key)){
            return null;
        }
        CacheEntry<String, V> entry = _cacheMap.get(key);
        entry.lastAccessed = System.currentTimeMillis();
        return entry.value;
    }
}
