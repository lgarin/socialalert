package com.bravson.socialalert.infrastructure.cache;

import javax.annotation.ManagedBean;
import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;
import javax.ws.rs.Produces;

@ManagedBean
public class CacheManagerProducer {

	private CachingProvider cachingProvider = Caching.getCachingProvider();
	private CacheManager cacheManager = cachingProvider.getCacheManager();
	
	@Produces
	public CacheManager getCacheManager() {
		return cacheManager;
	}
}
