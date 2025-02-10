package kai.cyber.assesment.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ScanResult {

    @JsonProperty("scanResults")
    private ScanResults scanResults;


    public ScanResults getScanResults() {
        return scanResults;
    }

    public void setScanResults(ScanResults scanResults) {
        this.scanResults = scanResults;
    }
}

