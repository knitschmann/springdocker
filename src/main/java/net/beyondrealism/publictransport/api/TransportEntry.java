package net.beyondrealism.publictransport.api;

/**
 * @author kni
 * created on: 06.09.17
 */
public class TransportEntry {

    protected String id;
    protected String priority;
    protected String adhoc;
    protected String timestamp; //UNIX format
    protected String starttime; //format dd.mm.yyyy hh:mm
    protected String endtime; //format dd.mm.yyyy hh:mm
    protected String line;
    protected String type; // 0-SBahn, 1-UBahn, 2-TRAM, 3-FERRY, 4-XPressBus, 5-TXL(AirportBus), 6-MetroBus, 7-NightBus, 8-Misc
    protected String direction;
    protected String from;
    protected String to;
    protected String reason;
    protected String consequence;

    private TransportEntry(Builder builder) {
        id = builder.id;
        priority = builder.priority;
        adhoc = builder.adhoc;
        timestamp = builder.timestamp;
        starttime = builder.starttime;
        endtime = builder.endtime;
        line = builder.line;
        type = builder.type;
        direction = builder.direction;
        from = builder.from;
        to = builder.to;
        reason = builder.reason;
        consequence = builder.consequence;
    }

    public static Builder newBuilder() {
        return new Builder();
    }


    public String getId() {
        return id;
    }

    public String getPriority() {
        return priority;
    }

    public String getAdhoc() {
        return adhoc;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getStarttime() {
        return starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public String getLine() {
        return line;
    }

    public String getType() {
        return type;
    }

    public String getDirection() {
        return direction;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getReason() {
        return reason;
    }

    public String getConsequence() {
        return consequence;
    }

    public static final class Builder {
        private String id;
        private String priority;
        private String adhoc;
        private String timestamp;
        private String starttime;
        private String endtime;
        private String line;
        private String type;
        private String direction;
        private String from;
        private String to;
        private String reason;
        private String consequence;

        private Builder() {
        }

        public Builder id(String val) {
            id = val;
            return this;
        }

        public Builder priority(String val) {
            priority = val;
            return this;
        }

        public Builder adhoc(String val) {
            adhoc = val;
            return this;
        }

        public Builder timestamp(String val) {
            timestamp = val;
            return this;
        }

        public Builder starttime(String val) {
            starttime = val;
            return this;
        }

        public Builder endtime(String val) {
            endtime = val;
            return this;
        }

        public Builder line(String val) {
            line = val;
            return this;
        }

        public Builder type(String val) {
            type = val;
            return this;
        }

        public Builder direction(String val) {
            direction = val;
            return this;
        }

        public Builder from(String val) {
            from = val;
            return this;
        }

        public Builder to(String val) {
            to = val;
            return this;
        }

        public Builder reason(String val) {
            reason = val;
            return this;
        }

        public Builder consequence(String val) {
            consequence = val;
            return this;
        }

        public TransportEntry build() {
            return new TransportEntry(this);
        }
    }

    @Override
    public String toString() {
        return "TransportEntry{" +
                "id='" + id + '\'' +
                ", priority='" + priority + '\'' +
                ", adhoc='" + adhoc + '\'' +
                ", timestamp='" + timestamp + '\'' +
                ", starttime='" + starttime + '\'' +
                ", endtime='" + endtime + '\'' +
                ", line='" + line + '\'' +
                ", type='" + type + '\'' +
                ", direction='" + direction + '\'' +
                ", from='" + from + '\'' +
                ", to='" + to + '\'' +
                ", reason='" + reason + '\'' +
                ", consequence='" + consequence + '\'' +
                '}';
    }
}
