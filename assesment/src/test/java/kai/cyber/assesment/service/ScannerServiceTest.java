package kai.cyber.assesment.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import kai.cyber.assesment.dao.entity.VulnerabilityEntity;
import kai.cyber.assesment.dao.repo.VulnerabilityRepository;
import kai.cyber.assesment.model.Vulnerability;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.ReflectionTestUtils.setField;

/**
 * Test class for ScannerService class
 */
@ExtendWith(MockitoExtension.class)
class ScannerServiceTest {

    @Mock
    private VulnerabilityRepository vulnerabilityRepository;
    @Mock
    private RestTemplate restTemplate;

    private ScannerService scannerService;

    @Mock
    private ExecutorService executorService;

    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        int corePoolSize = 2;
        int maxPoolSize = 4;
        long keepAliveTime = 1;
        int queueCapacity = 4;
        long shutdownTimeout = 1;

        scannerService = new ScannerService(corePoolSize, maxPoolSize, keepAliveTime, queueCapacity, shutdownTimeout);
        objectMapper = new ObjectMapper();

        setField(scannerService, "vulnerabilityRepository", vulnerabilityRepository);
        setField(scannerService, "restTemplate", restTemplate);
        setField(scannerService, "executorService", executorService);
        setField(scannerService, "objectMapper", objectMapper);

    }

    @Test
    void filterVulnerabilitiesBySeverity_ReturnsList() {
        String severity = "high";
        VulnerabilityEntity entity = new VulnerabilityEntity();
        entity.setDescription("test");
        entity.setCvss(111);
        List<VulnerabilityEntity> mockEntities = List.of(entity);
        when(vulnerabilityRepository.findBySeverity(severity)).thenReturn(mockEntities);
        List<Vulnerability> result = scannerService.filterVulnerabilitiesBySeverity(severity);
        assertFalse(result.isEmpty());
    }

    @Test
    void filterVulnerabilitiesBySeverity_EmptyList() {
        String severity = "low";
        when(vulnerabilityRepository.findBySeverity(severity)).thenReturn(List.of());
        List<Vulnerability> result = scannerService.filterVulnerabilitiesBySeverity(severity);
        assertTrue(result.isEmpty());
    }

    @Test
    void scanVulnerabilities_ThrowsException() throws Exception {
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            CompletableFuture.runAsync(task);
            return null;
        }).when(executorService).execute(any(Runnable.class));
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(ResponseEntity.ok("{\"scanResults\":{\"vulnerabilities\":[]}}"));
        assertThrows(Exception.class, () -> scannerService.scanVulnerabilities("repo", List.of("file1")));
    }

    @Test
    void scanVulnerabilities_Success() throws Exception {
        List<VulnerabilityEntity> entities = new ArrayList<>();
        doAnswer(invocation -> {
            Runnable task = invocation.getArgument(0);
            CompletableFuture.runAsync(task);
            return null;
        }).when(executorService).execute(any(Runnable.class));
        String jsonResponse = new String(Files.readAllBytes(Paths.get("src/test/resources/test-scan-results.json")));

        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(ResponseEntity.ok(jsonResponse));
        doReturn(entities).when(vulnerabilityRepository).saveAll(anyList());
        boolean result = scannerService.scanVulnerabilities("repo", List.of("file1", "file2"));
        assertTrue(result);
    }


}
