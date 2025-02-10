package kai.cyber.assesment.controller;

import kai.cyber.assesment.exception.BadRequestException;
import kai.cyber.assesment.exception.VulneribilityNotFoundException;
import kai.cyber.assesment.model.Vulnerability;
import kai.cyber.assesment.model.request.QueryRequest;
import kai.cyber.assesment.model.request.ScanRequest;
import kai.cyber.assesment.service.ScannerService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static org.springframework.http.HttpStatus.*;

/**
 * Test class for ScannerController class
 */
@ExtendWith(MockitoExtension.class)

public class ScannerControllerTest {

    @Mock
    private ScannerService service;

    @InjectMocks
    private ScannerController controller;

    @Test
    public void scanVulnerabilities_Success() throws Exception {
        ScanRequest request = new ScanRequest();
        request.setRepo("test-repo");
        request.setFiles(List.of("file1", "file2"));

        when(service.scanVulnerabilities(request.getRepo(), request.getFiles())).thenReturn(true);

        ResponseEntity<String> response = controller.scanVulnerabilities(request);

        assertEquals(CREATED, response.getStatusCode());
        assertEquals("Files are successfully downloaded", response.getBody());
    }

    @Test
    void scanVulnerabilities_Failure() throws Exception {
        ScanRequest request = new ScanRequest();
        request.setRepo("test-repo");
        request.setFiles(List.of("file1", "file2"));

        when(service.scanVulnerabilities(request.getRepo(), request.getFiles())).thenReturn(false);

        ResponseEntity<String> response = controller.scanVulnerabilities(request);

        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Files are not processed", response.getBody());
    }

    @Test
    void scanVulnerabilities_InvalidRequest() {
        ScanRequest request = new ScanRequest();

        assertThrows(BadRequestException.class, () -> controller.scanVulnerabilities(request));
    }

    @Test
    void queryVulnerabilities_Success() {
        QueryRequest request = new QueryRequest();
        request.setFilters(new QueryRequest.Filters());
        request.getFilters().setSeverity("high");

        List<Vulnerability> vulnerabilities = List.of(new Vulnerability());
        when(service.filterVulnerabilitiesBySeverity("high")).thenReturn(vulnerabilities);

        ResponseEntity<List<Vulnerability>> response = controller.queryVulnerabilities(request);

        assertEquals(OK, response.getStatusCode());
        assertEquals(vulnerabilities, response.getBody());
    }

    @Test
    void queryVulnerabilities_NotFound() {
        QueryRequest request = new QueryRequest();
        request.setFilters(new QueryRequest.Filters());
        request.getFilters().setSeverity("low");

        when(service.filterVulnerabilitiesBySeverity("low")).thenReturn(Collections.emptyList());

        assertThrows(VulneribilityNotFoundException.class, () -> controller.queryVulnerabilities(request));
    }

    @Test
    void queryVulnerabilities_InvalidRequest() {
        QueryRequest request = new QueryRequest();
        assertThrows(BadRequestException.class, () -> controller.queryVulnerabilities(request));
    }
}

