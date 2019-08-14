package no.nav.data.catalog.policies.test.component.consumer;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
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

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
@ActiveProfiles("test")
public class CodelistConsumerTest {
    private static final String DESCRIPTION = "Codelist description";

    @MockBean
    private RestTemplate restTemplate = mock(RestTemplate.class);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @InjectMocks
    private CodelistConsumer consumer;

    @Test
    public void getCodelistDescription() {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(new ResponseEntity<>(DESCRIPTION, HttpStatus.OK));
        String description =  consumer.getCodelistDescription(ListName.PURPOSE, "AAP");
        assertThat(description, is(DESCRIPTION));
    }

    @Test
    public void throwNotFound() {
        expectedException.expect(DataCatalogPoliciesNotFoundException.class);
        expectedException.expectMessage("Codelist (PURPOSE) with ID=AAP does not exist");
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
        consumer.getCodelistDescription(ListName.PURPOSE, "AAP");
    }

    @Test
    public void throwInternalServerError() {
        expectedException.expect(DataCatalogPoliciesTechnicalException.class);
        expectedException.expectMessage("Getting Codelist (PURPOSE: AAP) description failed with status=500 INTERNAL_SERVER_ERROR");
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        consumer.getCodelistDescription(ListName.PURPOSE, "AAP");
    }
}
