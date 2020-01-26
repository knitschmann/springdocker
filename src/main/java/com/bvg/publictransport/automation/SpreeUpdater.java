package com.bvg.publictransport.automation;

import com.bvg.publictransport.PublicTransportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Component that regulary updates the SpreeDataHandler in the background
 */
@Component
public class SpreeUpdater {
    private static final Logger LOG = LoggerFactory.getLogger(SpreeUpdater.class);

    private final PublicTransportService transportService;

    @Autowired
    public SpreeUpdater(PublicTransportService service) {
        this.transportService = service;
    }

    //every 5mins = 300000
    @Scheduled(initialDelay = 300000, fixedRate = 300000)
    public void update() {
        transportService.updateStationData("spree");
    }
}
