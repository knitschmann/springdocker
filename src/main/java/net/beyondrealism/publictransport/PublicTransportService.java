package net.beyondrealism.publictransport;

import net.beyondrealism.publictransport.api.LineInformation;
import net.beyondrealism.publictransport.api.TransportType;
import net.beyondrealism.publictransport.handler.PublicTransportHandler;
import net.beyondrealism.publictransport.handler.SpreeDataHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jmx.export.annotation.ManagedOperation;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Core component of the Public Transport Server.
 * Delegates all incoming requests to the publication specific handlers.
 * Is able to convert data from specific handlers to the proper format the REST controlelr is expecting to ensure a sorted representation via JSON Array.
 * Provides additional functions for force-updating internal datasets in production via JMX.
 */
@Component
public class PublicTransportService {
    private static final Logger LOG = LoggerFactory.getLogger(PublicTransportService.class);

    @Autowired
    private SpreeDataHandler spreeDataHandler;

    public List<LineInformation> getStationPublicTransportInfos(String station, TransportType type) {
        PublicTransportHandler transportHandler = determineHandler(station);
        if (transportHandler != null) {
            return transportHandler.showInfo(type);
        }

        LOG.error("unable to get transport infos for unknown station: " + station);
        return null;
    }

    public String getLastUpdateTime(String station) {
        PublicTransportHandler transportHandler = determineHandler(station);
        if (transportHandler != null) {
            return transportHandler.getLastUpdateTime();
        }

        LOG.error("unable to get last update time for unknown station: " + station);
        return "unknown for: " + station;
    }

    @ManagedOperation(description = "force update internal data of entries for given station by refetching information from external provider")
    public String updateStationData(String station) {
        boolean hasTriggeredUpdate = false;

        PublicTransportHandler transportHandler = determineHandler(station);
        if (transportHandler != null) {
            transportHandler.processExternalData();
            hasTriggeredUpdate = true;
        }

        if (hasTriggeredUpdate) {
            LOG.debug("triggered data update for station: " + station);
            return "updating data for: " + station;
        } else {
            LOG.error("unable to trigger update for unknown station: " + station);
            return "unable to update data for unknown station: " + station;
        }
    }

    private PublicTransportHandler determineHandler(String station) {
        switch (station.toLowerCase()) {
            case "spree":
                return spreeDataHandler;
            default:
                return null;
        }
    }
}
