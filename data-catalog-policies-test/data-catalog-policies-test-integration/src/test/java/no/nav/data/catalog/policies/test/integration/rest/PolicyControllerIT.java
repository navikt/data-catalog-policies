package no.nav.data.catalog.policies.test.integration.rest;

import com.github.tomakehurst.wiremock.http.HttpHeaders;
import no.nav.data.catalog.policies.app.AppStarter;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.domain.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.app.policy.rest.RestResponsePage;
import no.nav.data.catalog.policies.test.integration.IntegrationTestConfig;
import no.nav.data.catalog.policies.test.integration.PolicyTestContainer;
import no.nav.data.catalog.policies.test.integration.util.WiremockResponseTransformer;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertFalse;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {IntegrationTestConfig.class, AppStarter.class})
@Import(WiremockResponseTransformer.class)
@ActiveProfiles("test")
public class PolicyControllerIT {
    public static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    public static final String PURPOSE_CODE1 = "TEST1";
    public static final String INFORMATION_TYPE_NAME = "Sivilstand";

    public static final String POLICY_REST_ENDPOINT = "/policy/";

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private PolicyRepository policyRepository;

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer = PolicyTestContainer.getInstance();

    @Before
    public void setUp() {
        policyRepository.deleteAll();
    }

    @After
    public void cleanUp() {
        policyRepository.deleteAll();
    }

    @Test
    public void createPolicy() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME));
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(policyRepository.count(), is(1L));
        assertPolicy(createEntity.getBody().get(0), LEGAL_BASIS_DESCRIPTION1);
    }

    @Test
    public void createPolicyThrowNullableValidationException() {
        List<PolicyRequest> requestList = Arrays.asList(PolicyRequest.builder().build());
        ResponseEntity<String> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), String.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(createEntity.getBody(), containsString("purposeCode=purposeCode cannot be null"));
        assertThat(createEntity.getBody(), containsString("informationTypeName=informationTypeName cannot be null"));
        assertThat(createEntity.getBody(), containsString("egalBasisDescription=legalBasisDescription cannot be null"));
        assertThat(policyRepository.count(), is(0L));
    }

    @Test
    public void createPolicyThrowAlreadyExistsValidationException() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME));
        ResponseEntity<String> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), String.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(policyRepository.count(), is(1L));

        createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), String.class);

        assertThat(createEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(createEntity.getBody(), containsString("A policy combining InformationType " + INFORMATION_TYPE_NAME + " and Purpose " + PURPOSE_CODE1 + " already exists"));
        assertThat(policyRepository.count(), is(1L));
    }

    @Test
    public void createPolicyThrowDuplcateValidationException() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME), createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME));
        ResponseEntity<String> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), String.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(createEntity.getBody(), containsString("A request combining InformationType: " + INFORMATION_TYPE_NAME + " and Purpose: " + PURPOSE_CODE1 + " is not unique because it is already used in this request"));
    }

    @Test
    public void createPolicyThrowNotFoundValidationException() {
        createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME);
        List<PolicyRequest> requestList = Arrays.asList(PolicyRequest.builder().purposeCode("NOTFOUND").informationTypeName("NOTFOUND").legalBasisDescription(LEGAL_BASIS_DESCRIPTION1).build());
        ResponseEntity<String> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), String.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(createEntity.getBody(), containsString("The purposeCode NOTFOUND was not found in the PURPOSE codelist"));
        assertThat(createEntity.getBody(), containsString("informationTypeName=An informationType with name NOTFOUND does not exist"));
        assertThat(policyRepository.count(), is(0L));
    }

    @Test
    public void getOnePolicy() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME));
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));

        ResponseEntity<PolicyResponse> getEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/" + createEntity.getBody().get(0).getPolicyId(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), PolicyResponse.class);
        assertThat(getEntity.getStatusCode(), is(HttpStatus.OK));
        assertPolicy(getEntity.getBody(), LEGAL_BASIS_DESCRIPTION1);
        assertThat(policyRepository.count(), is(1L));
    }

    @Test
    public void getNotExistingPolicy() {
        ResponseEntity<Policy> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/-1", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Policy.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void updateOnePolicy() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME));
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));

        PolicyRequest request = requestList.get(0);
        request.setId(createEntity.getBody().get(0).getPolicyId());
        request.setLegalBasisDescription("UPDATED");
        ResponseEntity<PolicyResponse> updateEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/" + createEntity.getBody().get(0).getPolicyId(), HttpMethod.PUT, new HttpEntity<>(request), PolicyResponse.class);
        assertThat(updateEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(policyRepository.count(), is(1L));
        assertPolicy(updateEntity.getBody(), "UPDATED");
    }

    @Test
    public void updateOnePolicyThrowValidationExeption() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME));
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));

        PolicyRequest request = requestList.get(0);
        request.setLegalBasisDescription(null);
        ResponseEntity<String> updateEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/" + createEntity.getBody().get(0).getPolicyId(), HttpMethod.PUT, new HttpEntity<>(request), String.class);
        assertThat(updateEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(updateEntity.getBody(), containsString("legalBasisDescription=legalBasisDescription cannot be null"));
        assertThat(policyRepository.count(), is(1L));
    }

    @Test
    public void updateTwoPolices() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME), createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, "Postadresse"));
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertThat(createEntity.getBody().size(), is(2));
        assertThat(policyRepository.count(), is(2L));

        requestList.forEach(request -> request.setLegalBasisDescription("UPDATED"));
        requestList.get(0).setId(createEntity.getBody().get(0).getPolicyId());
        requestList.get(1).setId(createEntity.getBody().get(1).getPolicyId());

        ResponseEntity<List<PolicyResponse>> updateEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.PUT, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
                });
        assertThat(updateEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(updateEntity.getBody().size(), is(2));
        assertPolicy(updateEntity.getBody().get(0), "UPDATED");
        assertThat(updateEntity.getBody().get(1).getLegalBasisDescription(), is("UPDATED"));
        assertThat(policyRepository.count(), is(2L));
    }

    @Test
    public void updateThreePolicesThrowTwoExceptions() {
        List<PolicyRequest> requestList = Arrays.asList(
                createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME),
                createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, "Postadresse"),
                createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, "Arbeidsforhold")
        );
        ResponseEntity<List<PolicyResponse>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<PolicyResponse>>() {
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
                POLICY_REST_ENDPOINT + "policy", HttpMethod.PUT, new HttpEntity<>(requestList), String.class);
        assertThat(updateEntity.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(updateEntity.getBody(), containsString("Sivilstand/TEST1={legalBasisDescription=legalBasisDescription cannot be null}"));
        assertThat(updateEntity.getBody(), containsString("Postadresse/TEST1={legalBasisDescription=legalBasisDescription cannot be null}"));
        // No error reported regarding Arbeidsforhold/TEST1
        assertFalse(updateEntity.getBody().contains("Arbeidsforhold/TEST1"));
        assertThat(policyRepository.count(), is(3L));
    }

    @Test
    public void updateNotExistingPolicy() {
        PolicyRequest request = createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME);
        ResponseEntity<Policy> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/-1", HttpMethod.PUT, new HttpEntity<>(request), Policy.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deletePolicy() {
        List<PolicyRequest> requestList = Arrays.asList(createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, INFORMATION_TYPE_NAME));
        ResponseEntity<List<Policy>> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(requestList), new ParameterizedTypeReference<List<Policy>>() {
                });
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));

        ResponseEntity<String> deleteEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/" + createEntity.getBody().get(0).getPolicyId(), HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), String.class);
        assertThat(deleteEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(policyRepository.count(), is(0L));
    }

    @Test
    public void deleteNotExistingPolicy() {
        ResponseEntity<String> deleteEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/-1", HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), String.class);
        assertThat(deleteEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }


    @Test
    public void get20FirstPolicies() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<RestResponsePage<PolicyResponse>> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<RestResponsePage<PolicyResponse>>() {
                });
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(20));
        assertThat(responseEntity.getBody().getTotalElements(), is(100L));
        assertThat(responseEntity.getBody().getSize(), is(20));
        assertThat(policyRepository.count(), is(100L));
    }

    @Test
    public void get100Policies() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<RestResponsePage<PolicyResponse>> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy?page=0&size=100", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<RestResponsePage<PolicyResponse>>() {
                });
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(100));
        assertThat(responseEntity.getBody().getTotalElements(), is(100L));
        assertThat(responseEntity.getBody().getSize(), is(100));
        assertThat(policyRepository.count(), is(100L));
    }

    @Test
    public void countPolicies() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<Long> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/count", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Long.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), is(100L));
    }

    @Test
    public void getPoliciesPageBeyondMax() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<RestResponsePage<PolicyResponse>> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy?page=1&size=100", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<RestResponsePage<PolicyResponse>>() {
                });
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(0));
        assertThat(policyRepository.count(), is(100L));
    }

    @Test
    public void getPolicyForInformationType1() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<RestResponsePage<PolicyResponse>> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy?informationTypeId=1", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<RestResponsePage<PolicyResponse>>() {
                });
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(1));
        assertThat(policyRepository.count(), is(100L));
    }

    @Test
    public void countPolicyForInformationType1() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 100);

        ResponseEntity<Long> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/count?informationTypeId=1", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Long.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody(), is(1L));
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, int rows) {
        int i = 0;
        while (i < rows) {
            Policy policy = new Policy();
            policy.setInformationTypeId(Long.valueOf(i));
            policy.setLegalBasisDescription(legalBasisDescription);
            policy.setPurposeCode(purposeCode);
            policyRepository.save(policy);
            i++;
        }
    }

    private PolicyRequest createPolicyRequest(String legalBasisDescription, String purposeCode, String informationTypeName) {
        return PolicyRequest.builder().informationTypeName(informationTypeName).legalBasisDescription(legalBasisDescription).purposeCode(purposeCode).build();
    }

    private void assertPolicy(PolicyResponse policy, String legalBasisDescription) {
        assertThat(policy.getInformationType().getName(), is(INFORMATION_TYPE_NAME));
        assertThat(policy.getLegalBasisDescription(), is(legalBasisDescription));
        assertThat(policy.getPurpose().get("code"), is(PURPOSE_CODE1));
    }
}