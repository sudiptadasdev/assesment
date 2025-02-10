package kai.cyber.assesment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import kai.cyber.assesment.dao.entity.VulnerabilityEntity;
import kai.cyber.assesment.dao.repo.VulnerabilityRepository;
import kai.cyber.assesment.exception.FileProcessingException;
import kai.cyber.assesment.exception.GitHubConnectionException;
import kai.cyber.assesment.model.ScanResult;
import kai.cyber.assesment.model.Vulnerability;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class ScannerService {

    private final long shutdownTimeout;
    private final ExecutorService executorService ;

    private final RestTemplate restTemplate = new RestTemplate();
    @Autowired
    private  VulnerabilityRepository vulnerabilityRepository;

    @Autowired
    public ScannerService(
            @Value("${executor.corePoolSize}") int corePoolSize,
            @Value("${executor.maxPoolSize}") int maxPoolSize,
            @Value("${executor.keepAliveTime}") long keepAliveTime,
            @Value("${executor.queueCapacity}") int queueCapacity,
            @Value("${executor.shutdownTimeout}") long shutdownTimeout) {

        this.executorService = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                keepAliveTime,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueCapacity));
        this.shutdownTimeout=shutdownTimeout;
    }



    public List<Vulnerability> filterVulnerabilitiesBySeverity(String severity) {
       List<VulnerabilityEntity> vulnerabilityEntities=  vulnerabilityRepository.findBySeverity(severity);
       List<Vulnerability> vulnerabilities= new ArrayList<>();
       if (!vulnerabilityEntities.isEmpty()) {
           for (VulnerabilityEntity vulnerability : vulnerabilityEntities) {
               Vulnerability vul = new Vulnerability();
               vul.setRisk_factors(vulnerability.getRiskFactors());
               vul.setFixed_version(vulnerability.getFixedVersion());
               vul.setPublished_date(vulnerability.getPublishedDate());
               vul.setDescription(vulnerability.getDescription());
               vul.setSeverity(vulnerability.getSeverity());
               vul.setCvss(vulnerability.getCvss());
               vul.setStatus(vulnerability.getStatus());
               vul.setId(vulnerability.getId());
               vul.setLink(vulnerability.getLink());
               vul.setPackage_name(vulnerability.getPackageName());
               vul.setFixed_version(vulnerability.getFixedVersion());
               vul.setCurrent_version(vul.getCurrent_version());
               vulnerabilities.add(vul);
           }
       }

       return vulnerabilities;

    }
        public boolean scanVulnerabilities(String repo, List<String> fileNames) throws Exception {
         boolean success= false;
            List<CompletableFuture<Void>> futures = fileNames.stream()
                    .map(file -> CompletableFuture.runAsync(() -> processFile(repo, file), executorService))
                    .collect(Collectors.toList());
            waitForCompletionWithTimeout(futures, shutdownTimeout);
            success= true;
            return success;
        }

    private void waitForCompletionWithTimeout(List<CompletableFuture<Void>> futures, long timeout) throws FileProcessingException {
        try {

            CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .join();

        } catch (Exception e) {
            Throwable cause = e.getCause();
            if (cause instanceof GitHubConnectionException) {
                throw (GitHubConnectionException) cause;
            }
            throw new FileProcessingException("Error processing files", e.getCause());
        }
    }


    private void processFile(String repo, String file) {
        int attempts = 0;
        boolean success = false;
        while (attempts < 2 && !success) {
            try{
                ResponseEntity<String> response = restTemplate.getForEntity(repo + "/main/" + file, String.class);
                if (response.getStatusCode().is2xxSuccessful()) {
                    parseAndStoreVulnerabilities(response.getBody(), file);
                    success = true;
                } else {
                    throw new GitHubConnectionException("Failed to fetch file: " + file);
                }
            } catch (Exception e) {
                attempts++;
                if (attempts < 2 ) {
                    try {
                        TimeUnit.SECONDS.sleep(2);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                } else {
                    throw new GitHubConnectionException("Failed to connect to GitHub after multiple attempts", e);
                }
            }
        }
    }



    private void parseAndStoreVulnerabilities(String jsonResponse, String sourceFile) throws Exception {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                ScanResult[] scanResults = objectMapper.readValue(jsonResponse, ScanResult[].class);
                for (ScanResult scanResult : scanResults) {
                    List<VulnerabilityEntity>vulnerabilityList= new ArrayList<>();
                    for (Vulnerability vuln : scanResult.getScanResults().getVulnerabilities()) {
                        VulnerabilityEntity vulnerability = getVulnerability(sourceFile, scanResult, vuln);
                        vulnerabilityList.add(vulnerability);
                    }
                    vulnerabilityRepository.saveAll(vulnerabilityList);
                }
            } catch (Exception e) {
                throw new Exception("error occurred during saving files");

            }
        }

    private static VulnerabilityEntity getVulnerability(String sourceFile, ScanResult scanResult, Vulnerability vuln) {
        VulnerabilityEntity vulnerability = new VulnerabilityEntity();
        vulnerability.setId(vuln.getId());
        vulnerability.setSeverity(vuln.getSeverity());
        vulnerability.setCvss(vuln.getCvss());
        vulnerability.setStatus(vuln.getStatus());
        vulnerability.setPackageName(vuln.getPackage_name());
        vulnerability.setCurrentVersion(vuln.getCurrent_version());
        vulnerability.setFixedVersion(vuln.getFixed_version());
        vulnerability.setDescription(vuln.getDescription());
        vulnerability.setPublishedDate(vuln.getPublished_date());
        vulnerability.setLink(vuln.getLink());
        vulnerability.setRiskFactors(vuln.getRisk_factors());
        vulnerability.setSourceFile(sourceFile);
        vulnerability.setTimestamp(scanResult.getScanResults().getTimestamp());
        return vulnerability;
    }

    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(shutdownTimeout, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    }



