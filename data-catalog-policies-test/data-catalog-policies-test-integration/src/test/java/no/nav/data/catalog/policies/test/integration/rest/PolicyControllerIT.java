package no.nav.data.catalog.policies.test.integration.rest;

import com.github.tomakehurst.wiremock.http.HttpHeaders;
import no.nav.data.catalog.policies.app.AppStarter;
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
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        classes = {IntegrationTestConfig.class, AppStarter.class})
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@Transactional
public class PolicyControllerIT {
    public static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    public static final String PURPOSE_CODE1 = "PUR1";
    public static final String PURPOSE_DESCRIPTION1 = "Purpose 1";
    public static final String INFORMATION_TYPE_DESCRIPTION1 = "InformationType 1";

    public static final String POLICY_REST_EMDPOINT = "/policy/";

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
    public void getOnePolicy() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, INFORMATION_TYPE_DESCRIPTION1, 1);

        ResponseEntity<PagedResources<Policy>> responseEntity = restTemplate.exchange(
                POLICY_REST_EMDPOINT + "policy", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<PagedResources<Policy>>() {});
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(1));
    }

    @Test
    public void save100Get20Policies() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, INFORMATION_TYPE_DESCRIPTION1, 100);

        ResponseEntity<PagedResources<Policy>> responseEntity = restTemplate.exchange(
                POLICY_REST_EMDPOINT + "policy", HttpMethod.GET, new HttpEntity<>(new HttpHeaders()), new ParameterizedTypeReference<PagedResources<Policy>>() {});
        assertThat(responseEntity.getStatusCode(), is(HttpStatus.OK));
        assertThat(responseEntity.getBody().getContent().size(), is(20));
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, String purposeDescription, String informationTypeDescription, int rows) {
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
            InformationType informationType = informationTypeRepository.save(InformationType.builder().informationTypeId(new Long(i)).informationTypeName(informationTypeDescription).description(informationTypeDescription).build());
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
}