package com.bvg.publictransport.util;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Comparator that sorts the Lines of given TransportEntries with the following priority:
 * S-Bahn, U-Bahn, Tram, Ferry, X, TXL, M, N, numeric
 */
public class LineComparator implements Comparator<String> {

    private static final List<String> TRAMS = Arrays.asList(
            "M1", "M2", "M4", "M5", "M6", "M8", "M10", "M13", "M17",
            "12", "16", "18", "21", "27", "37", "50", "60", "61", "62", "63", "67", "68");

    @Override
    public int compare(String s1, String s2) {
        if (StringUtils.isBlank(s1) && StringUtils.isBlank(s2)) {
            return 0;
        } else {
            if (StringUtils.isNotBlank(s1)) {
                if (StringUtils.isNotBlank(s2)) {

                    String line1 = s1.toUpperCase();
                    String line2 = s2.toUpperCase();
                    int sBahnCompare = compareSBahn(line1, line2);
                    if (sBahnCompare == 0) {
                        int uBahnCompare = compareUBahn(line1, line2);
                        if (uBahnCompare == 0) {
                            int tramCompare = compareTram(line1, line2);
                            if (tramCompare == 0) {
                                int ferryCompare = compareFerry(line1, line2);
                                if (ferryCompare == 0) {
                                    int xpressCompare = compareXpress(line1, line2);
                                    if (xpressCompare == 0) {
                                        int txlCompare = compareTxl(line1, line2);
                                        if (txlCompare == 0) {
                                            int metroCompare = compareMetro(line1, line2);
                                            if (metroCompare == 0) {
                                                int nightCompare = compareNight(line1, line2);
                                                if (nightCompare == 0) {
                                                    return compareNumeric(line1, line2);
                                                } else {
                                                    return nightCompare;
                                                }
                                            } else {
                                                return metroCompare;
                                            }
                                        } else {
                                            return txlCompare;
                                        }
                                    } else {
                                        return xpressCompare;
                                    }
                                } else {
                                    return ferryCompare;
                                }
                            } else {
                                return tramCompare;
                            }
                        } else {
                            return uBahnCompare;
                        }
                    } else {
                        return sBahnCompare;
                    }


                } else {
                    return 1;
                }
            } else {
                return -1;
            }
        }
    }

    private int compareSBahn(String line1, String line2) {
        return compareLineIndicator(line1, line2, "S");
    }

    private int compareUBahn(String line1, String line2) {
        return compareLineIndicator(line1, line2, "U");
    }

    private int compareTram(String line1, String line2) {
        if (TRAMS.contains(line1) && TRAMS.contains(line2)) {
            return 0;
        } else if (TRAMS.contains(line1)) {
            return -1;
        } else if (TRAMS.contains(line2)) {
            return 1;
        } else {
            return 0;
        }
    }

    private int compareFerry(String line1, String line2) {
        return compareLineIndicator(line1, line2, "F");
    }

    private int compareXpress(String line1, String line2) {
        return compareLineIndicator(line1, line2, "X");
    }

    private int compareTxl(String line1, String line2) {
        if ("TXL".equalsIgnoreCase(line1) && "TXL".equalsIgnoreCase(line2)) {
            return 0;
        } else if ("TXL".equalsIgnoreCase(line1)) {
            return -1;
        } else if ("TXL".equalsIgnoreCase(line2)) {
            return 1;
        } else {
            return 0;
        }
    }

    private int compareMetro(String line1, String line2) {
        return compareLineIndicator(line1, line2, "M");
    }

    private int compareNight(String line1, String line2) {
        return compareLineIndicator(line1, line2, "N");
    }

    private int compareNumeric(String line1, String line2) {
        return new AlphanumComparator().compare(line1, line2);
    }

    private int compareLineIndicator(String line1, String line2, String lineSign) {
        if (StringUtils.startsWith(line1, lineSign) && StringUtils.startsWith(line2, lineSign)) {
            return 0;
        } else if (StringUtils.startsWith(line1, lineSign)) {
            return -1;
        } else if (StringUtils.startsWith(line2, lineSign)) {
            return 1;
        } else {
            return 0;
        }
    }
}
