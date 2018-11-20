package net.beyondrealism.publictransport.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of one transport line in any given public transport system (e.g. one train line or one bus) with a list of incident reports.
 * The wrapping of reports into this class makes it possible to return a JSON Array instead of an Object, ensuring sorted entries for any given REST endpoint to not loose their order.
 * If a LineInformation object is created, the list of entries it receives should already be sorted specific to the context it is used in (publication-specific or public-transport-provider specific, etc)
 */
public class LineInformation {
    private String line;
    private String type;
    private List<TransportEntry> entries = new ArrayList<>();

    public LineInformation(String line, List<TransportEntry> entries) {
        this.line = line;
        this.entries = entries;
    }

    public LineInformation(String line, String type, List<TransportEntry> entries) {
        this.line = line;
        this.type = type;
        this.entries = entries;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<TransportEntry> getEntries() {
        return entries;
    }

    public void setEntries(List<TransportEntry> entries) {
        this.entries = entries;
    }
}
