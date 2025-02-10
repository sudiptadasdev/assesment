package kai.cyber.assesment.model;

import java.util.ArrayList;


public class ScanMetadata {
    public String scanner_version;

    public String getScanner_version() {
        return scanner_version;
    }

    public void setScanner_version(String scanner_version) {
        this.scanner_version = scanner_version;
    }

    public String getPolicies_version() {
        return policies_version;
    }

    public void setPolicies_version(String policies_version) {
        this.policies_version = policies_version;
    }

    public ArrayList<String> getScanning_rules() {
        return scanning_rules;
    }

    public void setScanning_rules(ArrayList<String> scanning_rules) {
        this.scanning_rules = scanning_rules;
    }

    public ArrayList<String> getExcluded_paths() {
        return excluded_paths;
    }

    public void setExcluded_paths(ArrayList<String> excluded_paths) {
        this.excluded_paths = excluded_paths;
    }

    public String policies_version;
    public ArrayList<String> scanning_rules;
    public ArrayList<String> excluded_paths;
}
