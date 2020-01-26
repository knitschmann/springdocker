package com.bvg.publictransport.api;

public class TransportCacheKey {
    private String station;
    private TransportType type;

    public TransportCacheKey(String station, TransportType type) {
        this.station = station;
        this.type = type;
    }

    public String getStation() {
        return station;
    }

    public TransportType getType() {
        return type;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((station == null) ? 0 : station.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TransportCacheKey other = (TransportCacheKey) obj;
        if (station == null) {
            if (other.station != null)
                return false;
        } else if (!station.equals(other.station))
            return false;
        if (type == null) {
            if (other.type != null)
                return false;
        } else if (!type.equals(other.type))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "TransportCacheKey{" +
                "station='" + station + '\'' +
                ", type=" + type +
                '}';
    }
}
