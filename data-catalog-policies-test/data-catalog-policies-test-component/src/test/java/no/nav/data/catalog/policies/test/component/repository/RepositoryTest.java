package no.nav.data.catalog.policies.test.component.repository;

import lombok.AllArgsConstructor;
import no.nav.data.catalog.policies.app.policy.domain.InformationType;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.test.component.ComponentTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
@Transactional
public class RepositoryTest {
    private static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    private static final String PURPOSE_CODE1 = "PUR1";
    private static final String PURPOSE_DESCRIPTION1 = "Purpose 1";
    private static final String INFORMATION_TYPE_DESCRIPTION1 = "InformationType 1";
    private static final String INFORMATION_TYPE_NAME1 = "InformationTypeName1";

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired

    @Before
    public void setUp() {
        policyRepository.deleteAll();
//        TestTransaction.flagForCommit();
    }

    @After
    public void cleanUp() {
        policyRepository.deleteAll();
    }


    @Test
    public void getOne() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 1l, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
        assertThat(policyRepository.count(), is(1L));
    }

    @Test
    public void getAll() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 1L, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
        createTestdata("Legal basis 2", "PUR2", 2L, INFORMATION_TYPE_DESCRIPTION1, "InformationTypeName2");
        assertThat(policyRepository.count(), is(2L));
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, Long informationTypeId, String informationTypeDescription, String informationTypeName) {
        if (TestTransaction.isActive()) {
            TestTransaction.end();
        }

        InformationType informationType = InformationType.builder().id(informationTypeId).name(informationTypeName).build();

        Policy policy = new Policy();
        policy.setInformationTypeId(informationType.getId());
        policy.setPurposeCode(purposeCode);
        policy.setLegalBasisDescription(legalBasisDescription);
        policyRepository.save(policy);
    }

}
