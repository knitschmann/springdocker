package com.bvg.publictransport;

import com.bvg.publictransport.api.LineInformation;
import com.bvg.publictransport.api.TransportType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
public class PublicTransportRestController {
    private static final Logger LOG = LoggerFactory.getLogger(PublicTransportRestController.class);

    @Autowired
    private PublicTransportService transportService;

    @CrossOrigin
    @RequestMapping(value = "/{station}")
    public ResponseEntity<List<LineInformation>> getPublicTransportInfo(
            @PathVariable("station") String station) {

        LOG.debug("transport request for: " + station);
        List<LineInformation> result = transportService.getStationPublicTransportInfos(station, TransportType.ALL);

        if (result != null) {
            return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(result);
        }
        LOG.error("error retrieving transport entries for: " + station);
        return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(Collections.emptyList());
    }

    @CrossOrigin
    @RequestMapping(value = "/{station}/{type}")
    @ResponseBody
    public ResponseEntity<List<LineInformation>> getPublicTransportInfoForType(
            @PathVariable("station") String station,
            @PathVariable("type") String type) {

        TransportType transportType = TransportType.getFor(type);
        if (transportType != null) {
            LOG.debug("filtered transport request for: " + station + " of type:" + type);
            List<LineInformation> result = transportService.getStationPublicTransportInfos(station, transportType);

            if (result != null) {
                return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(result);
            }
            LOG.error("no cache entry of filtered transport entries for: " + station + "- transport type " + type + " not found");
            return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(Collections.emptyList());
        }
        LOG.error("error retrieving filtered transport entries for: " + station + "- transport type " + type + " not found");
        return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(Collections.emptyList());
    }

    @CrossOrigin
    @RequestMapping(value = "/{station}/refresh")
    public ResponseEntity<String> refreshPublicTransportInfo(
            @PathVariable("station") String station) {
        LOG.info("refresh data request for: " + station);
        return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(transportService.updateStationData(station));
    }

    @CrossOrigin
    @RequestMapping(value = "/{station}/lastUpdate")
    public ResponseEntity<String> getLastStationUpdateTime(
            @PathVariable("station") String station) {
        LOG.debug("last update request for: " + station);
        return ResponseEntity.ok().cacheControl(CacheControl.noCache()).body(transportService.getLastUpdateTime(station));
    }
}
