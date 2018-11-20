package net.beyondrealism.publictransport.util;

import net.beyondrealism.publictransport.api.TransportEntry;

import java.util.Comparator;

/**
 * sort TransportEntries by adhoc (true before false), then priority (descending), then timestamp (descending)
 */
public class TransportEntryComparator implements Comparator<TransportEntry> {

    @Override
    public int compare(TransportEntry t1, TransportEntry t2) {
        if (("true".equals(t1.getAdhoc()) && "true".equals(t2.getAdhoc()))
                || ("false".equals(t1.getAdhoc()) && "false".equals(t2.getAdhoc()))) {

            int prioCompare = t2.getPriority().compareTo(t1.getPriority());
            if (prioCompare == 0) {
                try {
                    Long t1Timestamp = Long.parseLong(t1.getTimestamp());
                    Long t2Timestamp = Long.parseLong(t2.getTimestamp());
                    return t2Timestamp.compareTo(t1Timestamp);
                } catch (NumberFormatException e) {
                    return 0;
                }
            } else {
                return prioCompare;
            }
        } else if ("true".equals(t1.getAdhoc())) {
            return -1;
        } else if ("true".equals(t2.getAdhoc())) {
            return 1;
        } else {
            return 0;
        }
    }
}
