package no.nav.data.catalog.policies.app.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static no.nav.data.catalog.policies.app.common.cache.CacheConfig.CODELIST_CACHE;

@Component
@Slf4j
public class CodelistConsumer {
    private static final Logger logger = LoggerFactory.getLogger(CodelistConsumer.class);

    @Autowired
    private RestTemplate restTemplate;

    @Value("${datacatalog.codelist.url}")
    private String codelistUrl;

    @Cacheable(cacheNames = CODELIST_CACHE, key = "#listName.name() + '-' + #code")
    public String getCodelistDescription(ListName listName, String code) {
        logger.debug("CodelistConsumer: About to get codelist description for ListName={} and code={}", listName.name(), code);
        if (code == null || listName == null) return null;
        try {
            ResponseEntity<String> responseEntity = restTemplate.getForEntity(codelistUrl + "/" + listName.name() + "/" + code.trim().toUpperCase(), String.class);
            return responseEntity.getBody();
        } catch (
                HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                logger.error(String.format("Codelist (" + listName.name() + ") with ID=%s does not exist", code.trim().toUpperCase()));
                throw new DataCatalogPoliciesNotFoundException(String.format("Codelist (" + listName.name() + ") with ID=%s does not exist", code.trim().toUpperCase()));
            } else {
                logger.error(String.format("Getting Codelist (" + listName.name() + ": %s) description failed with status=%s message=%s", code.trim().toUpperCase(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
                throw new DataCatalogPoliciesTechnicalException(String.format("Getting Codelist (" + listName.name() + ": %s) description failed with status=%s message=%s", code.trim().toUpperCase(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
            }
        } catch (HttpServerErrorException e) {
            logger.error(String.format("Getting Codelist (" + listName.name() + ": %s) description  failed with status=%s message=%s", code.trim().toUpperCase(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
            throw new DataCatalogPoliciesTechnicalException(String.format("Getting Codelist (" + listName.name() + ": %s) description  failed with status=%s message=%s", code.trim().toUpperCase(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
        }
    }
}
