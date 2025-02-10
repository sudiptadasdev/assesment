package kai.cyber.assesment.model.request;

import java.util.List;


public class ScanRequest {

    private String repo;
    private List<String> files;

    public String getRepo() {
        return repo;
    }

    public void setRepo(String repo) {
        this.repo = repo;
    }

    public List<String> getFiles() {
        return files;
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
}

