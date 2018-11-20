package net.beyondrealism.publictransport;

import net.beyondrealism.publictransport.cache.CacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PublicTransportCacheController {
    private static final Logger LOG = LoggerFactory.getLogger(PublicTransportCacheController.class);

    private final CacheService cacheService;

    @Autowired
    public PublicTransportCacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @RequestMapping(value = "/cache/purge", method = RequestMethod.GET)
    public String purgeCache() {
        cacheService.invalidateAll();
        LOG.warn("triggered manual cache purge");
        return "cache cleaned";
    }

    @RequestMapping(value = "/cache/stats", method = RequestMethod.GET)
    @ResponseBody
    public String cacheStats() {
        return cacheService.stats().toString();
    }

}
