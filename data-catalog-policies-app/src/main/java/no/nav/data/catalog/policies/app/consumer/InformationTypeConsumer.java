package no.nav.data.catalog.policies.app.consumer;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import no.nav.data.catalog.policies.app.policy.domain.InformationType;
import no.nav.data.catalog.policies.app.policy.domain.InformationTypeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import static no.nav.data.catalog.policies.app.common.cache.CacheConfig.INFORMATIONTYPEBYID_CACHE;
import static no.nav.data.catalog.policies.app.common.cache.CacheConfig.INFORMATIONTYPEBYNAME_CACHE;

@Component
public class InformationTypeConsumer {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${datacatalog.informationtype.url}")
    private String InformationTypeEndpointUrl;

    @Cacheable(cacheNames = INFORMATIONTYPEBYNAME_CACHE, key = "#informationTypeName")
    public InformationType getInformationTypeByName(String informationTypeName) {
        try {
            ResponseEntity<InformationTypeResponse> responseEntity = restTemplate.getForEntity(InformationTypeEndpointUrl + "/name/" + informationTypeName.trim()
                    , InformationTypeResponse.class);
            InformationTypeResponse response = responseEntity.getBody();
            return new InformationType().convertToInformationType(response);
        } catch (
                HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new DataCatalogPoliciesNotFoundException(String.format("InformationType with name=%s does not exist", informationTypeName.trim()));
            } else {
                throw new DataCatalogPoliciesTechnicalException(String.format("Getting InformationType failed with status=%s message=%s"
                        , informationTypeName.trim(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
            }
        } catch (HttpServerErrorException e) {
            throw new DataCatalogPoliciesTechnicalException(String.format("Getting InformationType with name %s failed with status=%s message=%s"
                    , informationTypeName.trim(), e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
        }
    }

    @Cacheable(cacheNames = INFORMATIONTYPEBYID_CACHE, key = "#informationTypeId")
    public InformationType getInformationTypeById(Long informationTypeId) {
        try {
            ResponseEntity<InformationTypeResponse> responseEntity = restTemplate.getForEntity(InformationTypeEndpointUrl + "/" + informationTypeId
                    , InformationTypeResponse.class);
            return new InformationType().convertToInformationType(responseEntity.getBody());
        } catch (
                HttpClientErrorException e) {
            if (HttpStatus.NOT_FOUND.equals(e.getStatusCode())) {
                throw new DataCatalogPoliciesNotFoundException(String.format("InformationType with id=%s does not exist", informationTypeId));
            } else {
                throw new DataCatalogPoliciesTechnicalException(String.format("Getting InformationType with id=%s failed with status=%s message=%s"
                        , informationTypeId, e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
            }
        } catch (HttpServerErrorException e) {
            throw new DataCatalogPoliciesTechnicalException(String.format("Getting InformationType with id %s failed with status=%s message=%s"
                    , informationTypeId, e.getStatusCode(), e.getResponseBodyAsString()), e, e.getStatusCode());
        }
    }
}