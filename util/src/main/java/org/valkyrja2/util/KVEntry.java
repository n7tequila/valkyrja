/*
 * PROJECT valkyrja2
 * util/KVEntry.java
 * Copyright (c) 2022 Tequila.Yang
 */

package org.valkyrja2.util;

import java.util.Map;
import java.util.Objects;

/**
 * key-value Entry
 *
 * @author Tequila
 * @create 2022/07/03 00:36
 **/
public final class KVEntry<K, V> implements Map.Entry<K, V> {

    /** key */
    private final K key;

    /** value */
    private V value;

    public KVEntry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public K getKey() {
        return key;
    }

    @Override
    public V getValue() {
        return value;
    }

    public final V setValue(V newValue) {
        V oldValue = value;
        value = newValue;
        return oldValue;
    }

    public final int hashCode() {
        return Objects.hashCode(key) ^ Objects.hashCode(value);
    }

    public final boolean equals(Object o) {
        if (o == this)
            return true;
        if (o instanceof Map.Entry) {
            Map.Entry<?,?> e = (Map.Entry<?,?>)o;
            return Objects.equals(key, e.getKey()) &&
                    Objects.equals(value, e.getValue());
        }
        return false;
    }
}
