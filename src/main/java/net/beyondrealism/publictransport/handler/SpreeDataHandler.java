package net.beyondrealism.publictransport.handler;

import net.beyondrealism.publictransport.api.LineInformation;
import net.beyondrealism.publictransport.api.TransportCacheKey;
import net.beyondrealism.publictransport.api.TransportEntry;
import net.beyondrealism.publictransport.api.TransportType;
import net.beyondrealism.publictransport.cache.CacheService;
import net.beyondrealism.publictransport.processor.BvgProcessor;
import net.beyondrealism.publictransport.util.TransportEntryComparator;
import org.apache.http.client.fluent.Request;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

import javax.annotation.PostConstruct;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static net.beyondrealism.publictransport.api.TransportType.*;

@Component
public class SpreeDataHandler implements PublicTransportHandler {

    private static final Logger LOG = LoggerFactory.getLogger(SpreeDataHandler.class);

    @Value("${spree.externalDataUrl}")
    private String url;
    @Value("${spree.connectionTimeout}")
    private int connectionTimeout;
    @Value("${spree.socketTimeout}")
    private int socketTimeout;

    private LocalDateTime lastUpdateTime;
    private final String STATION = "spree";

    private final BvgProcessor bvgProcessor;
    private final CacheService cacheService;

    @Autowired
    public SpreeDataHandler(BvgProcessor bvgProcessor, CacheService cacheService) {
        this.bvgProcessor = bvgProcessor;
        this.cacheService = cacheService;
    }

    @Override
    public List<LineInformation> showInfo() {
        return cacheService.get(new TransportCacheKey(STATION, ALL));
    }

    @Override
    public List<LineInformation> showInfo(TransportType type) {
        return cacheService.get(new TransportCacheKey(STATION, type));
    }

    @Override
    @PostConstruct
    public List<LineInformation> processExternalData() {
        LOG.info("start external data fetching for: spree");
        try {
            InputStream inputStream = Request.Get(url)
                    .connectTimeout(connectionTimeout)
                    .socketTimeout(socketTimeout)
                    .execute()
                    .returnContent()
                    .asStream();

            InputSource inputSource = new InputSource(inputStream);
            inputSource.setEncoding("UTF-8");
            return processData(inputSource);
        } catch (Exception e) {
            LOG.error("failed to fetch external data from: " + url, e);
            return null;
        }

    }

    public List<LineInformation> processData(InputSource source) {
        List<TransportEntry> newEntries = new ArrayList<>();

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = null;
        Document document = null;

        try {
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
            document = documentBuilder.parse(source);

            List<Node> rawEntries = bvgProcessor.collectEntryNodes(document);

            for (Node rawEntry : rawEntries) {
                TransportEntry entry = bvgProcessor.populateTransportEntry(rawEntry);

                if(isValidTransportEntry(entry)){
                    newEntries.add(entry);
                }
            }

            newEntries.sort(new TransportEntryComparator());
            List<LineInformation> allGroupedEntries = bvgProcessor.groupEntries(newEntries);

            updateCache(allGroupedEntries);
            lastUpdateTime = LocalDateTime.now();

            return allGroupedEntries;

        } catch (ParserConfigurationException e) {
            LOG.error("error configuring parser publication spree", e);
        } catch (Exception e) {
            LOG.error("error parsing data for publication spree", e);
        }
        LOG.error("error processing data for publication spree");
        return null;
    }

    @Override
    public String getLastUpdateTime() {
        return lastUpdateTime.toString();
    }

    /**
     * a TransportEntry can only be displayed when it can properly be grouped and sorted, therefor the line needs to be given and it has to belong to a known type type
     * @param entry
     * @return
     */
    private boolean isValidTransportEntry(TransportEntry entry) {
        return entry.getLine() != null && !"-1".equals(entry.getType());
    }

    private void updateCache(List<LineInformation> allGroupedEntries) {
        List<LineInformation> busEntries = bvgProcessor.filterEntries(allGroupedEntries, BUS);
        List<LineInformation> tramEntries = bvgProcessor.filterEntries(allGroupedEntries, TRAM);
        List<LineInformation> railwayEntries = bvgProcessor.filterEntries(allGroupedEntries, RAILWAY);
        List<LineInformation> subwayEntries = bvgProcessor.filterEntries(allGroupedEntries, SUBWAY);
        List<LineInformation> ferryEntries = bvgProcessor.filterEntries(allGroupedEntries, FERRY);
        List<LineInformation> metroBusEntries = bvgProcessor.filterEntries(allGroupedEntries, METRO_BUS);
        List<LineInformation> nightBusEntries = bvgProcessor.filterEntries(allGroupedEntries, NIGHT_BUS);
        List<LineInformation> txlEntries = bvgProcessor.filterEntries(allGroupedEntries, TXL_BUS);
        List<LineInformation> expressBusEntries = bvgProcessor.filterEntries(allGroupedEntries, EXPRESS_BUS);

        cacheService.put(new TransportCacheKey(STATION, ALL), allGroupedEntries);
        cacheService.put(new TransportCacheKey(STATION, BUS), busEntries);
        cacheService.put(new TransportCacheKey(STATION, TRAM), tramEntries);
        cacheService.put(new TransportCacheKey(STATION, RAILWAY), railwayEntries);
        cacheService.put(new TransportCacheKey(STATION, SUBWAY), subwayEntries);
        cacheService.put(new TransportCacheKey(STATION, FERRY), ferryEntries);
        cacheService.put(new TransportCacheKey(STATION, METRO_BUS), metroBusEntries);
        cacheService.put(new TransportCacheKey(STATION, NIGHT_BUS), nightBusEntries);
        cacheService.put(new TransportCacheKey(STATION, TXL_BUS), txlEntries);
        cacheService.put(new TransportCacheKey(STATION, EXPRESS_BUS), expressBusEntries);
    }
}
