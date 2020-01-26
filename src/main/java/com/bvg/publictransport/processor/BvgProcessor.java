package com.bvg.publictransport.processor;

import com.bvg.publictransport.api.LineInformation;
import com.bvg.publictransport.api.TransportEntry;
import com.bvg.publictransport.api.TransportType;
import com.bvg.publictransport.util.LineComparator;
import com.bvg.publictransport.util.LineInformationComparator;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class BvgProcessor implements TransportProcessor {

    private static final Logger LOG = LoggerFactory.getLogger(BvgProcessor.class);

    private static final List<String> TRAMS = Arrays.asList(
            "M1", "M2", "M4", "M5", "M6", "M8", "M10", "M13", "M17",
            "12", "16", "18", "21", "27", "37", "50", "60", "61", "62", "63", "67", "68");

    /**
     * creates a TransportEntry by filling all fields to the best of it's abilities,
     * putting null for absent values and merging date specifications to comply with the desired String date format of the API (see TransportEntry class for date format)
     *
     * @param obj
     * @return
     */
    @Override
    public TransportEntry populateTransportEntry(Object obj) {

        if (obj instanceof Node) {
            Node rawEntry = (Node) obj;

            String timestamp = prepareTimestamp(getNodeInfo(rawEntry, "Datum"));

            String startDate = getNodeInfo(rawEntry, "GueltigVonDatum");
            String startClock = getNodeInfo(rawEntry, "GueltigVonZeit");
            String endDate = getNodeInfo(rawEntry, "GueltigBisDatum");
            String endClock = getNodeInfo(rawEntry, "GueltigBisZeit");
            String startTime = convertDateFormat(startDate, startClock);
            String endTime = convertDateFormat(endDate, endClock);

            return TransportEntry.newBuilder()
                    .id(getNodeInfo(rawEntry, "MeldungsID"))
                    .priority(getNodeInfo(rawEntry, "Prioritaet"))
                    .adhoc(getNodeInfo(rawEntry, "IsAdhoc"))
                    .timestamp(timestamp)
                    .starttime(startTime)
                    .endtime(endTime)
                    .line(getNodeInfo(rawEntry, "Linie"))
                    .type(determineType(getNodeInfo(rawEntry, "Linie")))
                    .direction(getNodeInfo(rawEntry, "RichtungName"))
                    .from(getNodeInfo(rawEntry, "BeginnAbschnittName"))
                    .to(getNodeInfo(rawEntry, "EndeAbschnittName"))
                    .reason(getNodeInfo(rawEntry, "TextIntUrsache"))
                    .consequence(getNodeInfo(rawEntry, "TextIntAuswirkung"))
                    .build();
        }
        LOG.error("Failed to parse incoming BVG data, incoming object is not a proper XML Node");
        return null;
    }

    @Override
    public List<LineInformation> groupEntries(List<TransportEntry> entries) {
        TreeMap<String, List<TransportEntry>> groupedEntries = new TreeMap<>(new LineComparator());
        for (TransportEntry entry : entries) {
            if (StringUtils.isNotBlank(entry.getLine())) {
                groupedEntries.putIfAbsent(entry.getLine(), new ArrayList<>());
                groupedEntries.get(entry.getLine()).add(entry);
            }
        }

        List<LineInformation> lineInfos = new ArrayList<>();
        for (Map.Entry<String, List<TransportEntry>> entry : groupedEntries.entrySet()) {
            String key = entry.getKey();
            List<TransportEntry> value = entry.getValue();
            LineInformation lineInfo = new LineInformation(key, value);

            //cannot be null, a line without entries would not have been identified as a line otherwise
            lineInfo.setType(value.get(0).getType());

            lineInfos.add(lineInfo);
        }

        lineInfos.sort(new LineInformationComparator());

        return lineInfos;
    }

    @Override
    public List<LineInformation> filterEntries(List<LineInformation> entries, TransportType type) {
        List<LineInformation> filteredEntries = new ArrayList<>();
        switch (type) {
            case ALL:
                return entries;
            case BUS:
                filteredEntries = filterForBus(entries);
                break;
            case TRAM:
                filteredEntries = filterForTram(entries);
                break;
            default:
                filteredEntries = filterForOwnType(entries, type);
        }
        return filteredEntries;
    }

    /**
     * creates a list of all relevant xml nodes representing BVG entries, stripping unnecessary xml data in the process
     *
     * @param document from the external BVG endpoint as XML
     * @return xml nodes of BVG entries
     */
    public List<Node> collectEntryNodes(Document document) throws NullPointerException {
        List<Node> entries = new ArrayList<>();

        int entryCount = document.getElementsByTagName("ANDMeldung").getLength();
        for (int i = 0; i < entryCount; i++) {
            Node entry = document.getElementsByTagName("ANDMeldung").item(i);
            entries.add(entry);
        }
        return entries;
    }

    /**
     * helper method to prevent Nullpointer Exceptions when parsing text values for absent xml nodes.
     * Also performs basic cleaning of the input.
     * returns null either when xml key has no value or when key is missing compeltely
     *
     * @param node        the basic structure that holds desired information from external source like XML
     * @param elementName the desired elementName that needs to be retrieved
     * @return
     */
    private String getNodeInfo(Node node, String elementName) {
//        Node infoNode = ((DeferredElementImpl) node).getElementsByTagName(elementName).item(0);
//        if (infoNode != null) {
//            String textContent = infoNode.getTextContent();
//            return StringEscapeUtils.unescapeXml(textContent)
//                    .replaceAll("\n", "")
//                    .replace("<br/>", "")
//                    .replace("<br />", "");
//        }
//        return null;

        if (node.getNodeName().equals(elementName)) {
            return StringEscapeUtils.unescapeXml(node.getTextContent())
                    .replaceAll("\n", "")
                    .replace("<br/>", "")
                    .replace("<br />", "");
        } else {
            NodeList childNodes = node.getChildNodes();
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node childNode = childNodes.item(i);
                String content = getNodeInfo(childNode, elementName);
                if (content != null) {
                    return content;
                }
            }
        }
        return null;
    }

    String determineType(String line) {
        if (isSBahn(line)) {
            return "0";
        }
        if (isUBahn(line)) {
            return "1";
        }
        if (isTram(line)) {
            return "2";
        }
        if (isFerry(line)) {
            return "3";
        }
        if (isExpressBus(line)) {
            return "4";
        }
        if (isTXL(line)) {
            return "5";
        }
        if (isMetroBus(line)) {
            return "6";
        }
        if (isNightBus(line)) {
            return "7";
        }
        if (isBus(line)) {
            return "8";
        }

        return "-1";
    }

    private List<LineInformation> filterForBus(List<LineInformation> entries) {
        return entries
                .stream()
                .filter(entry -> isBus(entry.getLine()))
                .collect(Collectors.toList());
    }

    private List<LineInformation> filterForTram(List<LineInformation> entries) {
        return entries
                .stream()
                .filter(entry -> isTram(entry.getLine()))
                .collect(Collectors.toList());
    }

    /**
     * filters the given list of entries for the given type in a way that the type has a String representation which can be found in the entries to be filtered.
     * For example, a type can be TransportType.TEST with a String representation of "T", therefor all entries starting with "T" will be filtered and returned
     *
     * @param entries
     * @param type
     * @return
     */
    private List<LineInformation> filterForOwnType(List<LineInformation> entries, TransportType type) {
        return entries
                .stream()
                .filter(entry -> !isTram(entry.getLine()) && entry.getLine() != null && entry.getLine().startsWith(type.getValue()))
                .collect(Collectors.toList());
    }

    private boolean isSBahn(String line) {
        return StringUtils.startsWithIgnoreCase(line, "S");
    }

    private boolean isUBahn(String line) {
        return StringUtils.startsWithIgnoreCase(line, "U");
    }

    private boolean isTram(String line) {
        return TRAMS.contains(line);
    }

    private boolean isFerry(String line) {
        return StringUtils.startsWithIgnoreCase(line, "F");
    }

    private boolean isBus(String line) {
        return !isTram(line) && (StringUtils.isNumeric(line)
                || isExpressBus(line)
                || isTXL(line)
                || isMetroBus(line)
                || isNightBus(line));
    }

    private boolean isExpressBus(String line) {
        return StringUtils.startsWithIgnoreCase(line, "X");
    }

    private boolean isTXL(String line) {
        return "TXL".equalsIgnoreCase(line);
    }

    private boolean isMetroBus(String line) {
        return !isTram(line) && StringUtils.startsWithIgnoreCase(line, "M");
    }

    private boolean isNightBus(String line) {
        return StringUtils.startsWithIgnoreCase(line, "N");
    }

    private String prepareTimestamp(String date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
            sdf.setTimeZone(TimeZone.getTimeZone("Europe/Berlin"));
            return String.valueOf(sdf.parse(date).getTime() / 1000);
        } catch (ParseException e) {
            LOG.error("error parsing timestamp", e);
            return null;
        }
    }

    private String convertDateFormat(String date, String time) {
        try {
            DateFormat dateOriginalFormat = new SimpleDateFormat("yyyy-MM-dd");
            DateFormat dateDesiredFormat = new SimpleDateFormat("dd.MM.yyyy");

            DateFormat timeOriginalFormat = new SimpleDateFormat("HH:mm:ss");
            DateFormat timeDesiredFormat = new SimpleDateFormat("HH:mm");

            if (StringUtils.isNotBlank(date) && StringUtils.isNotBlank(time)) {
                Date dateStart = dateOriginalFormat.parse(date);
                Date timeStart = timeOriginalFormat.parse(time);
                return dateDesiredFormat.format(dateStart) + " " + timeDesiredFormat.format(timeStart);
            }
            LOG.debug("cannot convert date format for blank input");
            return null;

        } catch (ParseException e) {
            LOG.error("Parsing error for incoming BVG data - error converting date format", e);
            return null;
        }
    }
}
