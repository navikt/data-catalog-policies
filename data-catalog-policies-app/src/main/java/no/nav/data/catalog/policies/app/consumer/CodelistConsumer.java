package no.nav.data.catalog.policies.app.consumer;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
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

    @Value("${datacatalog.codelist.url}")
    private String codelistUrl;

    public String getCodelistDescription(ListName listName, String code) {
        if (code == null || listName == null) return null;
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(codelistUrl + "/" + listName.name() + "/" + code.trim().toUpperCase(), String.class);
            return responseEntity.getBody();
        } catch (
                HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new DataCatalogPoliciesNotFoundException(String.format("Codelist (" + listName.name() + ") with ID=%s does not exist", code.trim().toUpperCase()));
            } else {
                throw new DataCatalogPoliciesTechnicalException(String.format("Getting Codelist (" + listName.name() + ": %s) description failed with status=%s message=%s", code.trim().toUpperCase(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
            }
        } catch (HttpServerErrorException e) {
            throw new DataCatalogPoliciesTechnicalException(String.format("Getting Codelist (" + listName.name() + ": %s) description  failed with status=%s message=%s", code.trim().toUpperCase(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
        }
    }
}
