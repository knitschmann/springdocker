package com.bvg.publictransport.api;

import java.util.HashMap;
import java.util.Map;

public enum TransportType {
    //Collections of Types
    ALL("ALL"),
    BUS("BUS"),
    TRAM("TRAM"),

    //Railway
    RAILWAY("S"),

    //Underground
    SUBWAY("U"),

    //Ferries
    FERRY("F"),

    //Busses
    METRO_BUS("M"),
    NIGHT_BUS("N"),
    TXL_BUS("TXL"),
    EXPRESS_BUS("X");


    private final String value;
    private static Map<String, TransportType> map = new HashMap<>();

    static {
        for (TransportType type : TransportType.values()) {
            map.put(type.getValue(), type);
        }
    }

    /**
     * retrieve the enum representation for the given string if available
     * @param type
     * @return corresponding enum for given String
     */
    public static TransportType getFor(String type) {
        return map.get(type.toUpperCase());
    }

    TransportType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }


}
