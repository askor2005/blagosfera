package ru.radom.kabinet.util;

import java.util.AbstractMap;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MapsUtils {

    private MapsUtils() {
    }

    public static <K, V> Map.Entry<K, V> entry(K key, V value) {
        return new AbstractMap.SimpleEntry<>(key, value);
    }

    public static <K, U> Collector<Map.Entry<K, U>, ?, Map<K, U>> toMap() {
        return Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <K, U> Collector<Map.Entry<K, U>, ?, ConcurrentMap<K, U>> toConcurrentMap() {
        return Collectors.toConcurrentMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    public static <K, V> Map<K, V> map(Stream<Map.Entry<K, V>> stream) {
        return stream.collect(toMap());
    }

    public static <K, V> Map<K, V> map(Map<K, V> map) {
        return map(map.entrySet().stream());
    }

    public static <K, V> Map<K, V> concurrentMap(Stream<Map.Entry<K, V>> stream) {
        return stream.collect(toConcurrentMap());
    }

    public static <K, V> Map<K, V> concurrentMap(Map<K, V> map) {
        return concurrentMap(map.entrySet().stream());
    }

    public static <K, V> Map<K, V> unmodifiableMap(Stream<Map.Entry<K, V>> stream) {
        return Collections.unmodifiableMap(map(stream));
    }

    public static <K, V> Map<K, V> unmodifiableMap(Map<K, V> map) {
        return unmodifiableMap(map.entrySet().stream());
    }

    public static <K, V> Map<K, V> unmodifiableConcurrentMap(Stream<Map.Entry<K, V>> stream) {
        return Collections.unmodifiableMap(concurrentMap(stream));
    }

    public static <K, V> Map<K, V> unmodifiableConcurrentMap(Map<K, V> map) {
        return unmodifiableConcurrentMap(map.entrySet().stream());
    }
}
