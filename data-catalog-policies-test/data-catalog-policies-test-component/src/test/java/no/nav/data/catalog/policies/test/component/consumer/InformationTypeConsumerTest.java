package no.nav.data.catalog.policies.test.component.consumer;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import no.nav.data.catalog.policies.app.consumer.InformationTypeConsumer;
import no.nav.data.catalog.policies.app.policy.domain.InformationType;
import no.nav.data.catalog.policies.app.policy.domain.InformationTypeResponse;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class InformationTypeConsumerTest {
    private static final String DESCRIPTION = "InformationType description";
    private static final String NAME = "InformationType name";
    private static final Map CATEGORY = Map.of("code", "CAT1", "description","InformationType category");
    private static final Map PRODUCER = Map.of("code", "PROD", "description","InformationType producer");
    private static final Map SYSTEM = Map.of("code", "SYS1", "description","InformationType system");
    private static final Boolean PERSONAL_DATA = true;

    @MockBean
    private RestTemplate restTemplate = mock(RestTemplate.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private InformationTypeConsumer consumer;

    @Test
    public void getInformationTypeById() {
        InformationTypeResponse response = InformationTypeResponse.builder()
                .name(NAME)
                .description(DESCRIPTION)
                .category(CATEGORY)
                .producer(PRODUCER)
                .system(SYSTEM)
                .personalData(PERSONAL_DATA).build();
        when(restTemplate.getForEntity(anyString(), eq(InformationTypeResponse.class))).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        InformationType informationType =  consumer.getInformationTypeById(1L);
        assertInformationType(informationType);
    }

    @Test
    public void getByIdThrowNotFound() {
        expectedException.expect(DataCatalogPoliciesNotFoundException.class);
        expectedException.expectMessage("InformationType with id=1 does not exist");
        when(restTemplate.getForEntity(anyString(), eq(InformationTypeResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        consumer.getInformationTypeById(1L);
    }

    @Test
    public void getByIdThrowInternalServerError() {
        expectedException.expect(DataCatalogPoliciesTechnicalException.class);
        expectedException.expectMessage("Getting InformationType with id 1 failed with status=500 INTERNAL_SERVER_ERROR");
        when(restTemplate.getForEntity(anyString(), eq(InformationTypeResponse.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        consumer.getInformationTypeById(1L);
    }

    @Test
    public void getInformationTypeByName() {
        InformationTypeResponse response = InformationTypeResponse.builder()
                .name(NAME)
                .description(DESCRIPTION)
                .category(CATEGORY)
                .producer(PRODUCER)
                .system(SYSTEM)
                .personalData(PERSONAL_DATA).build();
        when(restTemplate.getForEntity(anyString(), eq(InformationTypeResponse.class))).thenReturn(new ResponseEntity<>(response, HttpStatus.OK));
        InformationType informationType =  consumer.getInformationTypeByName(NAME);
        assertInformationType(informationType);
    }

    @Test
    public void getByNameThrowNotFound() {
        expectedException.expect(DataCatalogPoliciesNotFoundException.class);
        expectedException.expectMessage("InformationType with name=InformationType name does not exist");
        when(restTemplate.getForEntity(anyString(), eq(InformationTypeResponse.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        consumer.getInformationTypeByName(NAME);
    }

    @Test
    public void getByNameThrowInternalServerError() {
        expectedException.expect(DataCatalogPoliciesTechnicalException.class);
        expectedException.expectMessage("Getting InformationType with name InformationType name failed with status=500 INTERNAL_SERVER_ERROR");
        when(restTemplate.getForEntity(anyString(), eq(InformationTypeResponse.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        consumer.getInformationTypeByName(NAME);
    }

    private void assertInformationType(InformationType informationType) {
        assertThat(informationType.getName(), is(NAME));
        assertThat(informationType.getDescription(), is(DESCRIPTION));
        assertThat(informationType.getPersonalData(), is(PERSONAL_DATA));
        assertThat(informationType.getCategoryCode(), is(CATEGORY.get("code")));
        assertThat(informationType.getProducerCode(), is(PRODUCER.get("code")));
        assertThat(informationType.getSystemCode(), is(SYSTEM.get("code")));
    }
}
