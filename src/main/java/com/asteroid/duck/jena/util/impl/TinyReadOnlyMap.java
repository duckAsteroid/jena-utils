package com.asteroid.duck.jena.util.impl;

import java.util.*;

/**
 * A {@link Map} implementation designed for low overhead when used with a small
 * number of keys (e.g. &lt; 10). The map is also read only.
 * @param <K> the key type
 * @param <V> the value type
 */
public class TinyReadOnlyMap<K,V> implements Map<K,V> {
    private final List<K> keys;
    private final List<V> values;

    /**
     * Create the tiny map with an array of keys and values.
     * NOTE: The constructor does not enforce uniquness ({@link Set} semantics) in the keys. First result wins.
     * @see #get(Object)
     * @param keys
     * @param values
     */
    public TinyReadOnlyMap(K[] keys, V[] values) {
        if (keys.length != values.length)
            throw new IllegalArgumentException("Keys and values cannot be different lengths");
        this.keys = Collections.unmodifiableList(Arrays.asList(keys));
        this.values = Collections.unmodifiableList(Arrays.asList(values));
    }

    public TinyReadOnlyMap(List<K> keys, List<V> values) {
        if (keys.size() != values.size())
            throw new IllegalArgumentException("Keys and values cannot be different lengths");
        this.keys = Collections.unmodifiableList(keys);
        this.values = Collections.unmodifiableList(values);
    }

    @Override
    public int size() {
        return keySet().size();
    }

    @Override
    public boolean isEmpty() {
        return keys.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return keys.contains(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return values.contains(value);
    }

    /**
     * Performs a simple linear search ({@link List#indexOf(Object)} for a given key
     * @param key the key to access the corresponding value
     * @return the value
     */
    @Override
    public V get(Object key) {
        int index = keys.indexOf(key);
        if (index < 0) return null;
        return values.get(index);
    }

    @Override
    public V put(K key, V value) {
        throw new UnsupportedOperationException("Read only");
    }

    @Override
    public V remove(Object key) {
        throw new UnsupportedOperationException("Read only");
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        throw new UnsupportedOperationException("Read only");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Read only");
    }

    @Override
    public Set<K> keySet() {
        return Collections.unmodifiableSet(new HashSet<>(keys));
    }

    @Override
    public Collection<V> values() {
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return Collections.unmodifiableSet(new AbstractSet<Entry<K, V>>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new EntryIterator(keySet().iterator());
            }

            @Override
            public int size() {
                return TinyReadOnlyMap.this.size();
            }
        });
    }

    private class EntryIterator implements Iterator<Entry<K, V>> {
        private final Iterator<K> keyIterator;

        private EntryIterator(Iterator<K> keyIterator) {
            this.keyIterator = keyIterator;
        }

        @Override
        public boolean hasNext() {
            return keyIterator.hasNext();
        }

        @Override
        public Entry<K, V> next() {
            final K key = keyIterator.next();
            final V value = get(key);
            return new Entry<K, V>() {
                @Override
                public K getKey() {
                    return key;
                }

                @Override
                public V getValue() {
                    return value;
                }

                @Override
                public V setValue(V value) {
                    throw new UnsupportedOperationException("Read only");
                }
            };
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Iterator<Entry<K, V>> iterator = entrySet().iterator();
        while(iterator.hasNext()) {
            Entry<K, V> entry = iterator.next();
            sb.append(entry.getKey()).append('=').append(entry.getValue());
            if (iterator.hasNext()) {
                sb.append(',');
            }
        }
        return sb.toString();
    }
}
