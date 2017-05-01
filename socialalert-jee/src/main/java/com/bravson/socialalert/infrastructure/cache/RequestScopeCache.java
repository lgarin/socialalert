package com.bravson.socialalert.infrastructure.cache;

import java.util.HashMap;
import java.util.function.Function;

import javax.annotation.ManagedBean;
import javax.enterprise.context.RequestScoped;

@ManagedBean
@RequestScoped
public class RequestScopeCache<K, V> {

	private final HashMap<K, V> cache = new HashMap<>();
	
	public V memoized(K key, Function<? super K, ? extends V> mappingFunction) {
		return cache.computeIfAbsent(key, mappingFunction);
	}
}
