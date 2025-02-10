package kai.cyber.assesment.model;

public class Summary {

    public boolean isCompliant() {
        return compliant;
    }

    public void setCompliant(boolean compliant) {
        this.compliant = compliant;
    }

    public int total_vulnerabilities;
    public SeverityCounts severity_counts;
    public int fixable_count;
    public boolean compliant;
}



