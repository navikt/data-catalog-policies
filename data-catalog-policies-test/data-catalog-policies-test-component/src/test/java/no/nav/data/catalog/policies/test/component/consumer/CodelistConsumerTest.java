package no.nav.data.catalog.policies.test.component.consumer;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import no.nav.data.catalog.policies.app.codelist.CodelistConsumer;
import no.nav.data.catalog.policies.app.codelist.domain.ListName;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class CodelistConsumerTest {

    private static final String DESCRIPTION = "Codelist description";

    @MockBean
    private RestTemplate restTemplate = mock(RestTemplate.class);

    @InjectMocks
    private CodelistConsumer consumer;

    @Test
    void getCodelistDescription() {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenReturn(new ResponseEntity<>(DESCRIPTION, HttpStatus.OK));
        String description = consumer.getCodelistDescription(ListName.PURPOSE, "AAP");
        assertEquals(DESCRIPTION, description);
    }

    @Test
    void throwNotFound() {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        var exception = assertThrows(DataCatalogPoliciesNotFoundException.class,
                () -> consumer.getCodelistDescription(ListName.PURPOSE, "AAP"));
        assertEquals("Codelist (PURPOSE) with ID=AAP does not exist", exception.getMessage());
    }

    @Test
    void throwInternalServerError() {
        when(restTemplate.getForEntity(anyString(), eq(String.class))).thenThrow(new HttpServerErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        var exception = assertThrows(DataCatalogPoliciesTechnicalException.class,
                () -> consumer.getCodelistDescription(ListName.PURPOSE, "AAP"));
        assertEquals("Getting Codelist (PURPOSE: AAP) description failed with status=500 INTERNAL_SERVER_ERROR message=", exception.getMessage());
    }
}
