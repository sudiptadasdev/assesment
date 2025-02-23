package kai.cyber.assesment.controller;

import kai.cyber.assesment.exception.BadRequestException;
import kai.cyber.assesment.exception.VulneribilityNotFoundException;
import kai.cyber.assesment.model.Vulnerability;
import kai.cyber.assesment.model.request.QueryRequest;
import kai.cyber.assesment.model.request.ScanRequest;
import kai.cyber.assesment.service.ScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller class which has two rest endpoint /scan and /query.
 * /scan endpoint takes repository and file names as input in form of ScanRequest, process it through ScannerService class, and returns
 * either success response or failure response.
 * /query takes filters as input in form of QueryRequest , searches the related files through ScannerService class and returns related files or Not found response
 */
@RestController
@RequestMapping("/api")
public class ScannerController {

    @Autowired
    private ScannerService service;


    @PostMapping("/scan")
    public ResponseEntity<String> scanVulnerabilities(@RequestBody ScanRequest request) throws Exception {
        boolean success = false;
        if (request.getRepo() == null || request.getFiles() == null || request.getFiles().isEmpty()) {
            throw new BadRequestException("Invalid request parameters");
        }
        success = service.scanVulnerabilities(request.getRepo(), request.getFiles());
        if (success) {
            return new ResponseEntity<>("Files are successfully downloaded", HttpStatus.CREATED);
        } else return new ResponseEntity<>("Files are not processed", HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @PostMapping("/query")
    public ResponseEntity<List<Vulnerability>> queryVulnerabilities(@RequestBody QueryRequest request) {
        if (request.getFilters() == null || request.getFilters().getSeverity() == null || request.getFilters().getSeverity().isEmpty()) {
            throw new BadRequestException("Invalid request parameters");
        }

        List<Vulnerability> vulnerabilities = service.filterVulnerabilitiesBySeverity(request.getFilters().getSeverity());

        if (vulnerabilities.isEmpty()) {
            throw new VulneribilityNotFoundException("No vulnerabilities found for given filter");
        }
        return new ResponseEntity<>(vulnerabilities, HttpStatus.OK);
    }

}


