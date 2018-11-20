package net.beyondrealism.publictransport.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import net.beyondrealism.publictransport.api.LineInformation;
import net.beyondrealism.publictransport.api.TransportCacheKey;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class CacheConfiguration {

    @Value("${cache.maxSize}")
    private String maximumSize;

    @Bean
    public Cache<TransportCacheKey, List<LineInformation>> cache() {
        Cache<TransportCacheKey, List<LineInformation>> cache = CacheBuilder.newBuilder()
                .maximumSize(NumberUtils.toLong(maximumSize, 100))
                .recordStats()
                .build();
        return cache;
    }
}
