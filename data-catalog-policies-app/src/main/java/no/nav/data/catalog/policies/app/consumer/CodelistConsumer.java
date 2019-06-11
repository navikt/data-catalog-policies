package no.nav.data.catalog.policies.app.consumer;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import no.nav.data.catalog.policies.app.policy.domain.Codelist;
import no.nav.data.catalog.policies.app.policy.domain.CodelistRequest;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
        try {
            ResponseEntity responseEntity = restTemplate.getForEntity(codelistUrl + "/" + listName.name() + "/" + code.trim().toUpperCase(), String.class);
            return responseEntity.getBody().toString();
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

    public Codelist createCodelist(ListName listName, String code, String description) {
        CodelistRequest request = new CodelistRequest(listName, code.trim().toUpperCase(), description);
        try {
            ResponseEntity<Codelist> responseEntity = restTemplate.exchange(codelistUrl, HttpMethod.POST, new HttpEntity<>(request), Codelist.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new DataCatalogPoliciesTechnicalException(String.format("creating codelist (" + listName.name() + ": %s) failed with status=%s message=%s", code.trim().toUpperCase(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
        }
    }

    public void deleteCodelist(ListName listName, String code) {
        try {
            restTemplate.exchange(codelistUrl + "/" + listName.name() + "/" + code.trim().toUpperCase(), HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        } catch (HttpClientErrorException | HttpServerErrorException e) {
            throw new DataCatalogPoliciesTechnicalException(String.format("Deleting codelist (" + listName.name() + ": %s) failed with status=%s message=%s", code.trim().toUpperCase(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
        }
    }

}
