package no.nav.data.catalog.policies.test.component.consumer;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import no.nav.data.catalog.policies.app.consumer.DatasetConsumer;
import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.domain.DatasetResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class DatasetConsumerTest {

    private static final String TITLE = "Dataset title";
    private static final String DATASET_ID_1 = "cd7f037e-374e-4e68-b705-55b61966b2fc";

    @MockBean
    private RestTemplate restTemplate = mock(RestTemplate.class);

    @InjectMocks
    private DatasetConsumer consumer;

    @Test
    void getDatasetById() {
        DatasetResponse response = DatasetResponse.builder().title(TITLE).id(UUID.randomUUID().toString()).build();
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        Dataset dataset = consumer.getDatasetById(DATASET_ID_1);
        assertDataset(dataset);
    }

    @Test
    void getByIdThrowNotFound() {
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        var exception = assertThrows(DataCatalogPoliciesNotFoundException.class,
                () -> consumer.getDatasetById(DATASET_ID_1));
        assertEquals("Dataset with id=cd7f037e-374e-4e68-b705-55b61966b2fc does not exist", exception.getMessage());
    }

    @Test
    void getByIdThrowInternalServerError() {
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        var exception = assertThrows(DataCatalogPoliciesTechnicalException.class,
                () -> consumer.getDatasetById(DATASET_ID_1));
        assertEquals("Getting Dataset with id=cd7f037e-374e-4e68-b705-55b61966b2fc failed with status=500 INTERNAL_SERVER_ERROR message=", exception.getMessage());
    }

    @Test
    void getDatasetByTitle() {
        DatasetResponse response = DatasetResponse.builder().title(TITLE).id(UUID.randomUUID().toString()).build();
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        Dataset dataset = consumer.getDatasetByTitle(TITLE);
        assertDataset(dataset);
    }

    @Test
    void getByTitleThrowNotFound() {
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        var exception = assertThrows(DataCatalogPoliciesNotFoundException.class,
                () -> consumer.getDatasetByTitle(TITLE));
        assertEquals("Dataset with title=Dataset title does not exist", exception.getMessage());
    }

    @Test
    void getByTitleThrowInternalServerError() {
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        var exception = assertThrows(DataCatalogPoliciesTechnicalException.class,
                () -> consumer.getDatasetByTitle(TITLE));
        assertEquals("Getting Dataset with title=Dataset title failed with status=500 INTERNAL_SERVER_ERROR message=", exception.getMessage());
    }

    private void assertDataset(Dataset dataset) {
        assertEquals(TITLE, dataset.getTitle());
    }
}
