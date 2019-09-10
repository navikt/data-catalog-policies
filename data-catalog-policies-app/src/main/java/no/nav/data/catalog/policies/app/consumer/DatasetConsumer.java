package no.nav.data.catalog.policies.app.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.domain.DatasetResponse;
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

import java.util.UUID;

import static no.nav.data.catalog.policies.app.common.cache.CacheConfig.DATASET_BY_ID_CACHE;
import static no.nav.data.catalog.policies.app.common.cache.CacheConfig.DATASET_BY_TITLE_CACHE;

@Component
@Slf4j
public class DatasetConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${datacatalog.dataset.url}")
    private String datasetEndpointUrl;

    @Cacheable(cacheNames = DATASET_BY_TITLE_CACHE)
    public Dataset getDatasetByTitle(String datasetTitle) {
        log.debug("DatasetConsumer: About to get Dataset by title={}", datasetTitle);
        try {
            ResponseEntity<DatasetResponse> responseEntity = restTemplate.getForEntity(String.format("%s/title/%s", datasetEndpointUrl, datasetTitle.trim())
                    , DatasetResponse.class);
            DatasetResponse response = responseEntity.getBody();
            return new Dataset().convertToDataset(response);
        } catch (
                HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new DataCatalogPoliciesNotFoundException(String.format("Dataset with title=%s does not exist", datasetTitle.trim()));
            } else {
                throw new DataCatalogPoliciesTechnicalException(String.format("Getting Dataset with title=%s failed with status=%s message=%s"
                        , datasetTitle.trim(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
            }
        } catch (HttpServerErrorException e) {
            throw new DataCatalogPoliciesTechnicalException(String.format("Getting Dataset with title=%s failed with status=%s message=%s"
                    , datasetTitle.trim(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
        }
    }

    @Cacheable(cacheNames = DATASET_BY_ID_CACHE)
    public Dataset getDatasetById(UUID datasetId) {
        log.debug("DatasetConsumer: About to get Dataset by id={}", datasetId);
        try {
            ResponseEntity<DatasetResponse> responseEntity = restTemplate.getForEntity(String.format("%s/%s", datasetEndpointUrl, datasetId), DatasetResponse.class);
            return new Dataset().convertToDataset(responseEntity.getBody());
        } catch (
                HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                log.error(String.format("Dataset with id=%s does not exist", datasetId));
                throw new DataCatalogPoliciesNotFoundException(String.format("Dataset with id=%s does not exist", datasetId));
            } else {
                return throwException(datasetId, e);
            }
        } catch (HttpServerErrorException e) {
            return throwException(datasetId, e);
        }
    }

    private Dataset throwException(UUID datasetId, HttpStatusCodeException e) {
        log.error(String.format("Getting Dataset with id=%s failed with status=%s message=%s"
                , datasetId, e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
        throw new DataCatalogPoliciesTechnicalException(String.format("Getting Dataset with id=%s failed with status=%s message=%s"
                , datasetId, e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
    }
}