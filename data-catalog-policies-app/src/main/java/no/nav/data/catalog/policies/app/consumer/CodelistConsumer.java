package no.nav.data.catalog.policies.app.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import static no.nav.data.catalog.policies.app.common.cache.CacheConfig.CODELIST_CACHE;

@Component
@Slf4j
public class CodelistConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${datacatalog.codelist.url}")
    private String codelistUrl;

    @Cacheable(cacheNames = CODELIST_CACHE, key = "#listName.name() + '-' + #code")
    public String getCodelistDescription(ListName listName, String code) {
        if (code == null || listName == null) {
            return null;
        }
        log.debug("CodelistConsumer: About to get codelist description for ListName={} and code={}", listName.name(), code);
        String codeTrimmed = code.trim().toUpperCase();
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(String.format("%s/%s/%s", codelistUrl, listName.name(), codeTrimmed), String.class);
            return responseEntity.getBody();
        } catch (HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                String err = String.format("Codelist (%s) with ID=%s does not exist", listName.name(), codeTrimmed);
                log.error(err);
                throw new DataCatalogPoliciesNotFoundException(err);
            } else {
                return throwException(listName, codeTrimmed, e);
            }
        } catch (HttpServerErrorException e) {
            return throwException(listName, codeTrimmed, e);
        }
    }

    private String throwException(ListName listName, String code, HttpStatusCodeException e) {
        var err = String.format("Getting Codelist (%s: %s) description failed with status=%s message=%s", listName.name(), code, e.getStatusCode(), e.getResponseBodyAsString());
        log.error(err, e);
        throw new DataCatalogPoliciesTechnicalException(err, e, e.getStatusCode());
    }
}
