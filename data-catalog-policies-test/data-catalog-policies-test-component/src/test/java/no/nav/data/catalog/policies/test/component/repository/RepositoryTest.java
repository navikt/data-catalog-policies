package no.nav.data.catalog.policies.test.component.repository;

import no.nav.data.catalog.policies.app.policy.domain.InformationType;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.test.component.ComponentTestConfig;
import no.nav.data.catalog.policies.test.component.PolicyTestContainer;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
public class RepositoryTest {
    private static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    private static final String PURPOSE_CODE1 = "PUR1";
    private static final String INFORMATION_TYPE_NAME1 = "InformationTypeName1";

    @ClassRule
    public static PolicyTestContainer postgreSQLContainer = PolicyTestContainer.getInstance();

    @Autowired
    private PolicyRepository policyRepository;

    @After
    public void setUp() {
        policyRepository.deleteAll();
    }

    @Test
    public void getOne() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 1l, INFORMATION_TYPE_NAME1);
        assertThat(policyRepository.count(), is(1L));
    }

    @Test
    public void getAll() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 1L, INFORMATION_TYPE_NAME1);
        createTestdata("Legal basis 2", "PUR2", 2L, "InformationTypeName2");
        assertThat(policyRepository.count(), is(2L));
    }

    @Test
    public void getByInformationType() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 1L, INFORMATION_TYPE_NAME1);
        createTestdata("Legal basis 2", "PUR2", 2L, "InformationTypeName2");
        assertThat(policyRepository.findByInformationTypeId(PageRequest.of(0, 10), 1L).getTotalElements(), is(1L));
        assertThat(policyRepository.findByInformationTypeId(PageRequest.of(0, 10), 2L).getTotalElements(), is(1L));
    }

    @Test
    public void countByInformationType() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 1L, INFORMATION_TYPE_NAME1);
        createTestdata("Legal basis 2", "PUR2", 2L, "InformationTypeName2");
        assertThat(policyRepository.countByInformationTypeId(1L), is(1L));
        assertThat(policyRepository.countByInformationTypeId(2L), is(1L));
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, Long informationTypeId, String informationTypeName) {
        InformationType informationType = InformationType.builder().informationTypeId(informationTypeId).name(informationTypeName).build();

        Policy policy = new Policy();
        policy.setInformationTypeId(informationType.getInformationTypeId());
        policy.setPurposeCode(purposeCode);
        policy.setLegalBasisDescription(legalBasisDescription);
        policyRepository.save(policy);
    }
}
