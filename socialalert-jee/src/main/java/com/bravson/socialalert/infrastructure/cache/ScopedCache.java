package com.bravson.socialalert.infrastructure.cache;

import java.util.HashMap;
import java.util.function.Function;

public abstract class ScopedCache<K, V> {

	private final HashMap<K, V> cache = new HashMap<>();
	
	public V memoized(K key, Function<? super K, ? extends V> mappingFunction) {
		return cache.computeIfAbsent(key, mappingFunction);
	}
}
