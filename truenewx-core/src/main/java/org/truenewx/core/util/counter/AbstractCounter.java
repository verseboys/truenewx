package org.truenewx.core.util.counter;

import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Spliterator;
import java.util.function.Consumer;

/**
 * 抽象计数器
 *
 * @author jianglei
 * @since JDK 1.8
 */
public abstract class AbstractCounter<K> implements Counter<K> {

    private Map<K, Integer> map;

    protected AbstractCounter(final Map<K, Integer> map) {
        this.map = map;
    }

    @Override
    public synchronized int add(final K key, final int step) {
        Integer count = this.map.get(key);
        if (count == null) {
            count = 0;
        }
        count += step;
        this.map.put(key, count);
        return count;
    }

    @Override
    public Integer remove(final K key) {
        return this.map.remove(key);
    }

    @Override
    public Integer count(final K key) {
        return this.map.get(key);
    }

    @Override
    public int size() {
        return this.map.size();
    }

    @Override
    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    @Override
    public Set<K> keySet() {
        return this.map.keySet();
    }

    @Override
    public Set<Entry<K, Integer>> entrySet() {
        return this.map.entrySet();
    }

    @Override
    public void toMap(final Map<K, Integer> map) {
        map.putAll(this.map);
    }

    @Override
    public Map<K, Integer> asMap() {
        return Collections.unmodifiableMap(this.map);
    }

    @Override
    public Iterator<Entry<K, Integer>> iterator() {
        return entrySet().iterator();
    }

    @Override
    public void forEach(final Consumer<? super Entry<K, Integer>> action) {
        entrySet().forEach(action);
    }

    @Override
    public Spliterator<Entry<K, Integer>> spliterator() {
        return entrySet().spliterator();
    }

}
