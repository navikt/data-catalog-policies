package no.nav.data.catalog.policies.app.consumer;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class CodelistConsumer {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${datacatalog.codelist.purpose.url}")
    private String purposeCodelistUrl;

    public String getPurposeCodelistDescription(String purposeCode) {
        try {
            ResponseEntity responseEntity = restTemplate.getForEntity(purposeCodelistUrl + "/" + purposeCode, String.class);
            return responseEntity.getBody().toString();
        } catch (
                HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new DataCatalogPoliciesNotFoundException(String.format("Codelist (PURPOSE) with ID=%s does not exist", purposeCode));
            } else {
                throw new DataCatalogPoliciesTechnicalException(String.format("Getting Codelist (PURPOSE: %s) description failed with status=%s message=%s", purposeCode, e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
            }
        } catch (HttpServerErrorException e) {
            throw new DataCatalogPoliciesTechnicalException(String.format("Getting Codelist (PURPOSE: %s) description  failed with status=%s message=%s", purposeCode, e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
        }
    }
}
