package no.nav.data.catalog.policies.test.integration.rest;

import com.github.tomakehurst.wiremock.http.HttpHeaders;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagService;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.domain.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.rest.RestResponsePage;
import no.nav.data.catalog.policies.test.integration.IntegrationTestBase;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class PolicyControllerIT extends IntegrationTestBase {

    private static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    private static final String PURPOSE_CODE1 = "TEST1";
    private static final String DATASET_TITLE = "Sivilstand";

    private static final String POLICY_REST_ENDPOINT = "/policy/";

    @Autowired
    protected TestRestTemplate restTemplate;

    @MockBean
    private BehandlingsgrunnlagService behandlingsgrunnlagService;

    @Test
    public void createPolicy() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_TITLE));
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(policyRepository.count(), is(1L));
        assertPolicy(createEntity.getBody().get(0), LEGAL_BASIS_DESCRIPTION1);
        verify(behandlingsgrunnlagService).scheduleDistributeForPurpose(PURPOSE_CODE1);
    }

    @Test
    public void createPolicyThrowNullableValidationException() {
        List<PolicyRequest> requestList = Arrays.asList(PolicyRequest.builder().build());
        ResponseEntity<String> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), String.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(createEntity.getBody(), containsString("purposeCode=purposeCode cannot be null"));
        assertThat(createEntity.getBody(), containsString("datasetTitle=datasetTitle cannot be null"));
        assertThat(createEntity.getBody(), containsString("legalBasisDescription=legalBasisDescription cannot be null"));
        assertThat(policyRepository.count(), is(0L));
    }

    @Test
    public void createPolicyThrowAlreadyExistsValidationException() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_TITLE));
        ResponseEntity<String> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), String.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(policyRepository.count(), is(1L));

        createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), String.class);

        assertThat(createEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(createEntity.getBody(), containsString("A policy combining Dataset " + DATASET_TITLE + " and Purpose " + PURPOSE_CODE1 + " already exists"));
        assertThat(policyRepository.count(), is(1L));
    }

    @Test
    public void createPolicyThrowDuplcateValidationException() {
        List<PolicyRequest> requestList = Arrays
                .asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_TITLE), createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1,
                        DATASET_TITLE));
        ResponseEntity<String> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), String.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(createEntity.getBody(),
                containsString("A request combining Dataset: " + DATASET_TITLE + " and Purpose: " + PURPOSE_CODE1 + " is not unique because it is already used in this request"));
    }

    @Test
    public void createPolicyThrowNotFoundValidationException() {
        createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_TITLE);
        List<PolicyRequest> requestList = Arrays
                .asList(PolicyRequest.builder().purposeCode("NOTFOUND").datasetTitle("NOTFOUND").legalBasisDescription(LEGAL_BASIS_DESCRIPTION1).build());
        ResponseEntity<String> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), String.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(createEntity.getBody(), containsString("The purposeCode NOTFOUND was not found in the PURPOSE codelist"));
        assertThat(createEntity.getBody(), containsString("datasetTitle=A dataset with title NOTFOUND does not exist"));
        assertThat(policyRepository.count(), is(0L));
    }

    @Test
    public void getOnePolicy() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_TITLE));
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));

        ResponseEntity<PolicyResponse> getEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + createEntity.getBody().get(0).getPolicyId(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), PolicyResponse.class);
        assertThat(getEntity.getStatusCode(), is(HttpStatus.OK));
        assertPolicy(getEntity.getBody(), LEGAL_BASIS_DESCRIPTION1);
        assertThat(policyRepository.count(), is(1L));
    }

    @Test
    public void getNotExistingPolicy() {
        ResponseEntity<Policy> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "-1", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Policy.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void updateOnePolicy() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_TITLE));
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));
        verify(behandlingsgrunnlagService).scheduleDistributeForPurpose(PURPOSE_CODE1);

        PolicyRequest request = requestList.get(0);
        request.setId(createEntity.getBody().get(0).getPolicyId());
        request.setLegalBasisDescription("UPDATED");
        ResponseEntity<PolicyResponse> updateEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + createEntity.getBody().get(0).getPolicyId(), HttpMethod.PUT, new HttpEntity<>(request), PolicyResponse.class);
        assertThat(updateEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(policyRepository.count(), is(1L));
        assertPolicy(updateEntity.getBody(), "UPDATED");
        verify(behandlingsgrunnlagService, times(2)).scheduleDistributeForPurpose(PURPOSE_CODE1);
    }

    @Test
    public void updateOnePolicyThrowValidationExeption() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_TITLE));
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));

        PolicyRequest request = requestList.get(0);
        request.setLegalBasisDescription(null);
        ResponseEntity<String> updateEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + createEntity.getBody().get(0).getPolicyId(), HttpMethod.PUT, new HttpEntity<>(request), String.class);
        assertThat(updateEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(updateEntity.getBody(), containsString("legalBasisDescription=legalBasisDescription cannot be null"));
        assertThat(policyRepository.count(), is(1L));
    }

    @Test
    public void updateTwoPolices() {
        List<PolicyRequest> requestList = Arrays
                .asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_TITLE), createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, "Postadresse"));
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(createEntity.getBody().size(), is(2));
        assertThat(policyRepository.count(), is(2L));

        requestList.forEach(request -> request.setLegalBasisDescription("UPDATED"));
        requestList.get(0).setId(createEntity.getBody().get(0).getPolicyId());
        requestList.get(1).setId(createEntity.getBody().get(1).getPolicyId());

        ResponseEntity<List<PolicyResponse>> updateEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(updateEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(updateEntity.getBody().size(), is(2));
        assertPolicy(updateEntity.getBody().get(0), "UPDATED");
        assertThat(updateEntity.getBody().get(1).getLegalBasisDescription(), is("UPDATED"));
        assertThat(policyRepository.count(), is(2L));
        verify(behandlingsgrunnlagService, times(2)).scheduleDistributeForPurpose(PURPOSE_CODE1);
    }

    @Test
    public void updateThreePolicesThrowTwoExceptions() {
        List<PolicyRequest> requestList = Arrays.asList(
                createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_TITLE),
                createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, "Postadresse"),
                createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, "Arbeidsforhold")
        );
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(createEntity.getBody().size(), is(3));

        requestList.get(0).setLegalBasisDescription(null);
        requestList.get(1).setLegalBasisDescription(null);
        requestList.get(2).setLegalBasisDescription("UPDATED");
        requestList.get(0).setId(createEntity.getBody().get(0).getPolicyId());
        requestList.get(1).setId(createEntity.getBody().get(1).getPolicyId());
        requestList.get(2).setId(createEntity.getBody().get(2).getPolicyId());

        ResponseEntity<String> updateEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.PUT, new HttpEntity<>(requestList), String.class);
        assertThat(updateEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(updateEntity.getBody(), containsString("Sivilstand/TEST1={legalBasisDescription=legalBasisDescription cannot be null}"));
        assertThat(updateEntity.getBody(), containsString("Postadresse/TEST1={legalBasisDescription=legalBasisDescription cannot be null}"));
        // No error reported regarding Arbeidsforhold/TEST1
        assertFalse(updateEntity.getBody().contains("Arbeidsforhold/TEST1"));
        assertThat(policyRepository.count(), is(3L));
    }

    @Test
    public void updateNotExistingPolicy() {
        PolicyRequest request = createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_TITLE);
        ResponseEntity<Policy> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "-1", HttpMethod.PUT, new HttpEntity<>(request), Policy.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deletePolicy() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, DATASET_TITLE));
        ResponseEntity<List<Policy>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<Policy>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));
        verify(behandlingsgrunnlagService).scheduleDistributeForPurpose(PURPOSE_CODE1);

        ResponseEntity<String> deleteEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + createEntity.getBody().get(0).getPolicyId(), HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), String.class);
        assertThat(deleteEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(policyRepository.count(), is(0L));
        verify(behandlingsgrunnlagService, times(2)).scheduleDistributeForPurpose(PURPOSE_CODE1);
    }

    @Test
    public void deleteNotExistingPolicy() {
        ResponseEntity<String> deleteEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "-1", HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), String.class);
        assertThat(deleteEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }


    @Test
    public void get20FirstPolicies() {
        createPolicy(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<RestResponsePage<PolicyResponse>> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT, HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<RestResponsePage<PolicyResponse>>() {
                });
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(20));
        assertThat(responseEntity.getBody().getTotalElements(), is(100L));
        assertThat(responseEntity.getBody().getPageSize(), is(20));
        assertThat(policyRepository.count(), is(100L));
    }

    @Test
    public void get100Policies() {
        createPolicy(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<RestResponsePage<PolicyResponse>> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "?pageNumber=0&pageSize=100", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<RestResponsePage<PolicyResponse>>() {
                });
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(100));
        assertThat(responseEntity.getBody().getTotalElements(), is(100L));
        assertThat(responseEntity.getBody().getPageSize(), is(100));
        assertThat(policyRepository.count(), is(100L));
    }

    @Test
    public void countPolicies() {
        createPolicy(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<Long> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "count", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Long.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), is(100L));
    }

    @Test
    public void getPoliciesPageBeyondMax() {
        createPolicy(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<RestResponsePage<PolicyResponse>> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "?pageNumber=1&pageSize=100", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<RestResponsePage<PolicyResponse>>() {
                });
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(0));
        assertThat(policyRepository.count(), is(100L));
    }

    @Test
    public void getPolicyForDataset1() {
        createPolicy(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<RestResponsePage<PolicyResponse>> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "?datasetId={id}", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()),
                new ParameterizedTypeReference<RestResponsePage<PolicyResponse>>() {
                }, DATASET_ID_1);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(1));
        assertThat(policyRepository.count(), is(100L));
    }

    @Test
    public void countPolicyForDataset1() {
        createPolicy(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<Long> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "count?datasetId={id}", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Long.class, DATASET_ID_1);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), is(1L));
    }

    private PolicyRequest createPolicyRequest(String legalBasisDescription, String purposeCode, String datasetTitle) {
        return PolicyRequest.builder().datasetTitle(datasetTitle).legalBasisDescription(legalBasisDescription).purposeCode(purposeCode).build();
    }

    private void assertPolicy(PolicyResponse policy, String legalBasisDescription) {
        assertThat(policy.getDataset().getTitle(), is(DATASET_TITLE));
        assertThat(policy.getLegalBasisDescription(), is(legalBasisDescription));
        assertThat(policy.getPurpose().getCode(), is(PURPOSE_CODE1));
    }
}