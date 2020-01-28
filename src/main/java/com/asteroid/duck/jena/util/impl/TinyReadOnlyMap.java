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
        return keys.size();
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

    @Override
    public V get(Object key) {
        return values.get(keys.indexOf(key));
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
        return new HashSet<>(keys);
    }

    @Override
    public Collection<V> values() {
        return values;
    }

    @Override
    public Set<Entry<K, V>> entrySet() {
        return new AbstractSet<Entry<K, V>>() {
            @Override
            public Iterator<Entry<K, V>> iterator() {
                return new Iterator<Entry<K, V>>() {
                    int i = 0;

                    @Override
                    public boolean hasNext() {
                        return i < size();
                    }

                    @Override
                    public Entry<K, V> next() {
                        final K key = keys.get(i);
                        final V value = values.get(i);
                        i++;
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
                };
            }

            @Override
            public int size() {
                return keys.size();
            }
        };
    }
}
