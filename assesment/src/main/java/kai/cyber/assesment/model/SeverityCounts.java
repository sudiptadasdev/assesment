package kai.cyber.assesment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class SeverityCounts {
    @JsonProperty("CRITICAL")
    public int critical;
    @JsonProperty("HIGH")
    public int high;
    @JsonProperty("MEDIUM")
    public int medium;

    public int getCritical() {
        return critical;
    }

    public void setCritical(int critical) {
        this.critical = critical;
    }

    public int getHigh() {
        return high;
    }

    public void setHigh(int high) {
        this.high = high;
    }

    public int getMedium() {
        return medium;
    }

    public void setMedium(int medium) {
        this.medium = medium;
    }

    public int getLow() {
        return low;
    }

    public void setLow(int low) {
        this.low = low;
    }

    @JsonProperty("LOW")
    public int low;
}
