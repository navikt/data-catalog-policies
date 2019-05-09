package no.nav.data.catalog.policies.test.integration.rest;

import com.github.tomakehurst.wiremock.http.HttpHeaders;
import no.nav.data.catalog.policies.app.AppStarter;
import no.nav.data.catalog.policies.app.policy.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import no.nav.data.catalog.policies.app.policy.entities.LegalBasis;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.entities.Purpose;
import no.nav.data.catalog.policies.app.policy.repository.InformationTypeRepository;
import no.nav.data.catalog.policies.app.policy.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.app.policy.repository.PurposeRepository;
import no.nav.data.catalog.policies.test.integration.IntegrationTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.Duration;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {IntegrationTestConfig.class, AppStarter.class})
@ActiveProfiles("itest")
@AutoConfigureWireMock(port = 0)
@Transactional
@ContextConfiguration(initializers = {PolicyControllerIT.Initializer.class})
public class PolicyControllerIT {
    public static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    public static final String PURPOSE_CODE1 = "PUR1";
    public static final String PURPOSE_DESCRIPTION1 = "Purpose 1";
    public static final String INFORMATION_TYPE_DESCRIPTION1 = "InformationType 1";
    public static final String INFORMATION_TYPE_NAME = "InformationTypeName";

    public static final String POLICY_REST_ENDPOINT = "/policy/";

    @Autowired
    protected TestRestTemplate restTemplate;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private LegalBasisRepository legalBasisRepository;

    @Autowired
    private PurposeRepository purposeRepository;

    @Autowired
    private InformationTypeRepository informationTypeRepository;

    @ClassRule
    public static PostgreSQLContainer postgreSQLContainer =
            (PostgreSQLContainer) new PostgreSQLContainer("postgres:10.4")
                    .withDatabaseName("sampledb")
                    .withUsername("sampleuser")
                    .withPassword("samplepwd")
                    .withStartupTimeout(Duration.ofSeconds(600));


    @Before
    public void setUp() {
        policyRepository.deleteAll();
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
        informationTypeRepository.deleteAll();
    }

    @After
    public void cleanUp() {
        policyRepository.deleteAll();
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
        informationTypeRepository.deleteAll();
    }

    @Test
    public void createPolicy() {
        PolicyRequest request = createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME);
        ResponseEntity<Policy> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(request), Policy.class);
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.CREATED));
        assertPolicy(responseEntity.getBody(), LEGAL_BASIS_DESCRIPTION1);
    }

    @Test
    public void getOnePolicy() {
        PolicyRequest request = createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME);
        ResponseEntity<Policy> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(request), Policy.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));

        createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/" + createEntity.getBody().getPolicyId(), HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Policy.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.OK));
        assertPolicy(createEntity.getBody(), LEGAL_BASIS_DESCRIPTION1);
    }

    @Test
    public void getNotExistingPolicy() {
        ResponseEntity<Policy> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/-1", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), Policy.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void updatePolicy() {
        PolicyRequest request = createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME);
        ResponseEntity<Policy> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(request), Policy.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));

        request.setLegalBasisDescription("UPDATED");
        createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/" + createEntity.getBody().getPolicyId(), HttpMethod.PUT, new HttpEntity<>(request), Policy.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.OK));
        assertPolicy(createEntity.getBody(), "UPDATED");
    }

    @Test
    public void updateNotExistingPolicy() {
        PolicyRequest request = createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME);
        ResponseEntity<Policy> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/-1", HttpMethod.PUT, new HttpEntity<>(request), Policy.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.NOT_FOUND));
    }

    @Test
    public void deletePolicy() {
        PolicyRequest request = createPolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME);
        ResponseEntity<Policy> createEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.POST, new HttpEntity<>(request), Policy.class);
        assertThat(createEntity.getStatusCode(), is(HttpStatus.CREATED));

        ResponseEntity<String> deleteEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy/" + createEntity.getBody().getPolicyId(), HttpMethod.DELETE, new HttpEntity<>(new HttpHeaders()), String.class);
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
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, INFORMATION_TYPE_DESCRIPTION1,INFORMATION_TYPE_NAME, 100);

        ResponseEntity<PagedResources<Policy>> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<PagedResources<Policy>>() {});
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(20));
    }

    @Test
    public void get100Policies() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, INFORMATION_TYPE_DESCRIPTION1,INFORMATION_TYPE_NAME, 100);

        ResponseEntity<PagedResources<Policy>> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy?page=0&size=100", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<PagedResources<Policy>>() {});
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(100));
    }

    @Test
    public void getPoliciesPageBeyondMax() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, INFORMATION_TYPE_DESCRIPTION1,INFORMATION_TYPE_NAME, 100);

        ResponseEntity<PagedResources<Policy>> responseEntity = restTemplate.exchange(
                POLICY_REST_ENDPOINT + "policy?page=1&size=100", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<PagedResources<Policy>>() {});
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(0));
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, String purposeDescription, String informationTypeDescription, String informationTypeName, int rows) {
        int i = 0;
        while (i < rows) {
            if (TestTransaction.isActive()) {
                TestTransaction.end();
            }
            TestTransaction.start();
            LegalBasis lb = legalBasisRepository.save(LegalBasis.builder().description(legalBasisDescription).build());

            Purpose purpose = new Purpose();
            purpose.setPurposeCode(purposeCode + i);
            purpose.setDescription(purposeDescription);
            purposeRepository.save(purpose);
            InformationType informationType = informationTypeRepository.save(InformationType.builder().informationTypeId(new Long(i)).name(informationTypeName + i).description(informationTypeDescription).build());
            TestTransaction.flagForCommit();
            TestTransaction.end();

            Policy policy = new Policy();
            policy.setInformationType(informationType);
            policy.setLegalBasis(lb);
            policy.setPurpose(purpose);
            policyRepository.save(policy);
            i++;
        }
    }

    private PolicyRequest createPolicyRequest(String legalBasisDescription, String purposeCode, String purposeDescription, String informationTypeDescription, String informationTypeName) {
        if (TestTransaction.isActive()) {
            TestTransaction.end();
        }
        TestTransaction.start();
        LegalBasis lb = legalBasisRepository.save(LegalBasis.builder().description(legalBasisDescription).build());

        Purpose purpose = new Purpose();
        purpose.setPurposeCode(purposeCode);
        purpose.setDescription(purposeDescription);
        purposeRepository.save(purpose);
        InformationType informationType = informationTypeRepository.save(InformationType.builder().informationTypeId(1L).name(informationTypeName).description(informationTypeDescription).build());
        TestTransaction.flagForCommit();
        TestTransaction.end();
        return PolicyRequest.builder().informationTypeId(informationType.getInformationTypeId()).legalBasisDescription(lb.getDescription()).legalBasisId(lb.getLegalBasisId()).purposeId(purpose.getPurposeId()).build();
    }

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    private void assertPolicy(Policy policy, String legalBasisDescription) {
        assertThat(policy.getInformationType().getDescription(), is(INFORMATION_TYPE_DESCRIPTION1));
        assertThat(policy.getInformationType().getName(), is(INFORMATION_TYPE_NAME));
        assertThat(policy.getLegalBasisDescription(), is(legalBasisDescription));
        assertThat(policy.getLegalBasis().getDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policy.getPurpose().getDescription(), is(PURPOSE_DESCRIPTION1));
        assertThat(policy.getPurpose().getPurposeCode(), is(PURPOSE_CODE1));
    }
}