package com.bvg.publictransport.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheStats;
import com.bvg.publictransport.api.LineInformation;
import com.bvg.publictransport.api.TransportCacheKey;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Manages the entire Cache of the Microservices - offers enough space for all publications to put pre-filtered entries according to transport types
 * (e.g. entries for busses for publication spree, entries for subways for another publication)
 */
@Service
public class CacheService {
    private static final Logger LOG = LoggerFactory.getLogger(CacheService.class);

    private final Cache<TransportCacheKey, List<LineInformation>> cache;

    @Autowired
    public CacheService(Cache<TransportCacheKey, List<LineInformation>> cache) {
        this.cache = cache;
    }

    public void put(TransportCacheKey key, List<LineInformation> infos) {
        cache.put(key, infos);
    }

    public List<LineInformation> get(TransportCacheKey key) {
        List<LineInformation> cachedEntry = cache.getIfPresent(key);
        if (cachedEntry == null) {
            LOG.error("No cache entry found for publication: " + key.getStation() + " and type: " + key.getType());
        }
        return cachedEntry;
    }

    public void invalidate(TransportCacheKey key) {
        cache.invalidate(key);
    }

    public void invalidateAll() {
        cache.invalidateAll();
    }

    public CacheStats stats() {
        return cache.stats();
    }

}
