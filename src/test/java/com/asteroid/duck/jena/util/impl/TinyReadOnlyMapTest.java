package com.asteroid.duck.jena.util.impl;

import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static com.asteroid.duck.jena.util.ResultStreamTest.assertResultEqual;
import static org.junit.Assert.*;

public class TinyReadOnlyMapTest {
    private static final String[] KEYS = new String[] {"A", "B", "C"};
    private static final Integer[] VALUES = new Integer[] {1, 2, 3};
    public static final String[] DUPLICATE_KEYS = {"A", "A", "B"};

    private TinyReadOnlyMap<String, Integer> subject;

    @Before
    public void setup() {
        subject = new TinyReadOnlyMap<>(KEYS, VALUES);
    }

    @Test
    public void testBadConstructorArgs() {
        assertThrows(NullPointerException.class, () -> {
            subject = new TinyReadOnlyMap<>(null, VALUES);
        });

        assertThrows(NullPointerException.class, () -> {
            subject = new TinyReadOnlyMap<>(KEYS, null);
        });

        assertThrows(IllegalArgumentException.class, () -> {
            subject = new TinyReadOnlyMap<>(new String[] {"A"}, VALUES);
        });
    }

    @Test
    public void size() {
        assertEquals(KEYS.length, subject.size());

        subject = new TinyReadOnlyMap<String, Integer>(DUPLICATE_KEYS, VALUES);
        assertEquals(2, subject.size());
    }

    @Test
    public void isEmpty() {
        assertFalse(subject.isEmpty());
        subject = new TinyReadOnlyMap<>(new String[0], new Integer[0]);
        assertTrue(subject.isEmpty());
    }

    @Test
    public void containsKey() {
        for(String key : KEYS) {
            assertTrue(subject.containsKey(key));
        }
        assertFalse(subject.containsKey("wibble"));
        assertFalse(subject.containsKey(null));
    }

    @Test
    public void containsValue() {
        for(Integer value : VALUES) {
            assertTrue(subject.containsValue(value));
        }
        assertFalse(subject.containsValue(99));
        assertFalse(subject.containsValue(null));
    }

    @Test
    public void get() {
        for (int i = 0; i < KEYS.length; i++) {
            assertEquals(VALUES[i], subject.get(KEYS[i]));
        }
    }

    @Test
    public void put() {
        assertThrows(UnsupportedOperationException.class, () -> {
            subject.put("wibble", 99);
        });
    }

    @Test
    public void remove() {
        assertThrows(UnsupportedOperationException.class, () -> {
            subject.remove("A");
        });
    }

    @Test
    public void putAll() {
        assertThrows(UnsupportedOperationException.class, () -> {
            subject.putAll(Collections.emptyMap());
        });
    }

    @Test
    public void clear() {
        assertThrows(UnsupportedOperationException.class, () -> {
            subject.clear();
        });
    }

    @Test
    public void keySet() {
        Set<String> keySet = subject.keySet();
        assertResultEqual(KEYS, keySet);

        subject = new TinyReadOnlyMap<>(DUPLICATE_KEYS, VALUES);
        keySet = subject.keySet();
        assertResultEqual(new String[]{"A", "B"}, keySet);
        assertUnmodifiable(keySet);
    }

    @Test
    public void values() {
        Collection<Integer> values = subject.values();
        assertUnmodifiable(values);
        assertResultEqual(VALUES, values);
    }

    @Test
    public void entrySet() {
        Set<Map.Entry<String, Integer>> entrySet = subject.entrySet();
        assertUnmodifiable(entrySet);
        List<String> keyList = Arrays.asList(KEYS);
        for(Map.Entry<String, Integer> entry : entrySet) {
            int index = keyList.indexOf(entry.getKey());
            assertSame(KEYS[index], entry.getKey());
            assertSame(VALUES[index], entry.getValue());
        }
        // duplicate keys
        subject = new TinyReadOnlyMap<>(DUPLICATE_KEYS, VALUES );
        keyList = Arrays.asList(DUPLICATE_KEYS);
        entrySet = subject.entrySet();
        assertEquals(subject.size(), entrySet.size());
        for(Map.Entry<String, Integer> entry : entrySet) {
            int index = keyList.indexOf(entry.getKey());
            assertSame(DUPLICATE_KEYS[index], entry.getKey());
            assertSame(VALUES[index], entry.getValue());
        }


    }

    @Test
    public void testToString() {
        assertEquals("A=1,B=2,C=3", subject.toString());

        subject = new TinyReadOnlyMap<>(DUPLICATE_KEYS, VALUES);
        assertEquals("A=1,B=3", subject.toString());
    }

    public static void assertUnmodifiable(Collection collection) {
        assertThrows(UnsupportedOperationException.class, () -> {
            collection.clear();
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            collection.add(new Object());
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            collection.addAll(Collections.emptyList());
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            collection.remove(new Object());
        });

        assertThrows(UnsupportedOperationException.class, () -> {
            collection.removeAll(Collections.emptyList());
        });
    }
}