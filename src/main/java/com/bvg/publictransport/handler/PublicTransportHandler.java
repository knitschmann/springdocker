package com.bvg.publictransport.handler;

import com.bvg.publictransport.api.LineInformation;
import com.bvg.publictransport.api.TransportType;

import java.util.List;

/**
 * A TransportHandler is responsible for providing a sorted representation of entries for a given publication.
 * To fetch data from external API's and providers, a handler uses a processor for parsing.
 * TransportHandlers can utilize the services of multiple processor to achieve full feature coverage
 * (e.g. Spreeradio Handler might use BVG as a source via a BVGProcessor and infos for Deutsche Bahn with another Processor in the future)
 */
public interface PublicTransportHandler {

    /**
     * displays all available and previously processed data of the handler for the specific publication.
     * The entries have to be sorted.
     * If showInfo displays outdated data, the data should be refreshed by processing external data again
     * @return all available entries in sorted order
     */
    public List<LineInformation> showInfo();

    /**
     * displays all available and previously processed data of the handler for the specific type of transport.
     * The entries have to be sorted.
     * @param type
     * @return all type-specific entries in sorted order
     */
    public List<LineInformation> showInfo(TransportType type);

    /**
     * updates internal datasets of the handler for the specific publication by calling and consuming external API of given provider
     * @return
     */
    public List<LineInformation> processExternalData();

    /**
     * displays the timestamp the internal data of the publication specific handler was last updated
     * @return Date of last successful update/import as String representation
     */
    public String getLastUpdateTime();
}
