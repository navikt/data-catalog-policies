package no.nav.data.catalog.policies.test.component.consumer;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import no.nav.data.catalog.policies.app.consumer.DatasetConsumer;
import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.domain.DatasetResponse;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class DatasetConsumerTest {

    private static final String DESCRIPTION = "Dataset description";
    private static final String TITLE = "Dataset title";
    private static final Map CATEGORY = Map.of("code", "CAT1", "description", "Dataset category");
    private static final Map PRODUCER = Map.of("code", "PROD", "description", "Dataset producer");
    private static final Map SYSTEM = Map.of("code", "SYS1", "description", "Dataset system");
    private static final Boolean PERSONAL_DATA = true;
    private static final UUID DATASET_ID_1 = UUID.fromString("cd7f037e-374e-4e68-b705-55b61966b2fc");

    @MockBean
    private RestTemplate restTemplate = mock(RestTemplate.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private DatasetConsumer consumer;

    @Test
    public void getDatasetById() {
        DatasetResponse response = DatasetResponse.builder().title(TITLE).id(UUID.randomUUID()).build();
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        Dataset dataset = consumer.getDatasetById(DATASET_ID_1);
        assertDataset(dataset);
    }

    @Test
    public void getByIdThrowNotFound() {
        expectedException.expect(DataCatalogPoliciesNotFoundException.class);
        expectedException.expectMessage("Dataset with id=cd7f037e-374e-4e68-b705-55b61966b2fc does not exist");
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        consumer.getDatasetById(DATASET_ID_1);
    }

    @Test
    public void getByIdThrowInternalServerError() {
        expectedException.expect(DataCatalogPoliciesTechnicalException.class);
        expectedException.expectMessage("Getting Dataset with id=cd7f037e-374e-4e68-b705-55b61966b2fc failed with status=500 INTERNAL_SERVER_ERROR");
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        consumer.getDatasetById(DATASET_ID_1);
    }

    @Test
    public void getDatasetByTitle() {
        DatasetResponse response = DatasetResponse.builder().title(TITLE).id(UUID.randomUUID()).build();
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        Dataset dataset = consumer.getDatasetByTitle(TITLE);
        assertDataset(dataset);
    }

    @Test
    public void getByTitleThrowNotFound() {
        expectedException.expect(DataCatalogPoliciesNotFoundException.class);
        expectedException.expectMessage("Dataset with title=Dataset title does not exist");
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        consumer.getDatasetByTitle(TITLE);
    }

    @Test
    public void getByTitleThrowInternalServerError() {
        expectedException.expect(DataCatalogPoliciesTechnicalException.class);
        expectedException.expectMessage("Getting Dataset with title=Dataset title failed with status=500 INTERNAL_SERVER_ERROR");
        when(restTemplate.getForEntity(anyString(), eq(DatasetResponse.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        consumer.getDatasetByTitle(TITLE);
    }

    private void assertDataset(Dataset dataset) {
        assertThat(dataset.getTitle(), is(TITLE));
    }
}
