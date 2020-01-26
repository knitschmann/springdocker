package com.bvg.publictransport.processor;

import com.bvg.publictransport.api.LineInformation;
import com.bvg.publictransport.api.TransportEntry;
import com.bvg.publictransport.api.TransportType;

import java.util.List;

/**
 * A TransportProcessor is able to generate TransportEntries from a single external source.
 * It manages incoming data and extracts relevant information, wrapping it in API Objects for further internal usage
 */
public interface TransportProcessor {

    /**
     * processes external data of whatever form and transforms it into server-readable TransportEntries
     * @param entry
     * @return
     */
    public TransportEntry populateTransportEntry(Object entry);

    /**
     * groups a list of BVG entries by line by using LineInformation API Object and sorts them
     *
     * @param ungroupedEntries
     * @return
     */
    public List<LineInformation> groupEntries(List<TransportEntry> ungroupedEntries);

    /**
     * filters the given list for specific bus/train/etc lines according to the TransportType
     *
     * @param entries
     * @param type
     * @return filtered entries of specified type
     */
    public List<LineInformation> filterEntries(List<LineInformation> entries, TransportType type);
}
