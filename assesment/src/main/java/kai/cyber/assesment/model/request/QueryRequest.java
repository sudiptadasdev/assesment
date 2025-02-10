package kai.cyber.assesment.model.request;

import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;


public class QueryRequest {
    @JsonProperty("filters")
    private Filters filters;

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {

        this.filters = filters;
    }


    public static class Filters {
        private String severity;


        public String getSeverity() {
            return severity;
        }

        public void setSeverity(String severity) {

            this.severity = severity;
        }
    }
}

