package kai.cyber.assesment.model;

import java.util.ArrayList;
import java.util.Date;

public class ScanResults {
    public String scan_id;
    public Date timestamp;

    public String getScan_id() {
        return scan_id;
    }

    public void setScan_id(String scan_id) {
        this.scan_id = scan_id;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getScan_status() {
        return scan_status;
    }

    public void setScan_status(String scan_status) {
        this.scan_status = scan_status;
    }

    public String getResource_type() {
        return resource_type;
    }

    public void setResource_type(String resource_type) {
        this.resource_type = resource_type;
    }

    public String getResource_name() {
        return resource_name;
    }

    public void setResource_name(String resource_name) {
        this.resource_name = resource_name;
    }

    public ArrayList<Vulnerability> getVulnerabilities() {
        return vulnerabilities;
    }

    public void setVulnerabilities(ArrayList<Vulnerability> vulnerabilities) {
        this.vulnerabilities = vulnerabilities;
    }

    public Summary getSummary() {
        return summary;
    }

    public void setSummary(Summary summary) {
        this.summary = summary;
    }

    public ScanMetadata getScan_metadata() {
        return scan_metadata;
    }

    public void setScan_metadata(ScanMetadata scan_metadata) {
        this.scan_metadata = scan_metadata;
    }

    public String scan_status;
    public String resource_type;
    public String resource_name;
    public ArrayList<Vulnerability> vulnerabilities;
    public Summary summary;
    public ScanMetadata scan_metadata;
}