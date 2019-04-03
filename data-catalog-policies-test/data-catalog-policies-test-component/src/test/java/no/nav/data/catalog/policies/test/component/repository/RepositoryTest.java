package no.nav.data.catalog.policies.test.component.repository;

import no.nav.data.catalog.policies.app.model.entities.LegalBasis;
import no.nav.data.catalog.policies.app.model.entities.Policy;
import no.nav.data.catalog.policies.app.model.entities.Purpose;
import no.nav.data.catalog.policies.app.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.repository.PolicyRepository;
import no.nav.data.catalog.policies.app.repository.PurposeRepository;
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

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
@Transactional
public class RepositoryTest {
    private static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    private static final String PURPOSE_CODE1 = "PUR1";
    private static final String PURPOSE_DESCRIPTION1 = "Purpose 1";

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private PurposeRepository purposeRepository;

    @Autowired
    private LegalBasisRepository legalBasisRepository;

    @Before
    public void setUp() {
        policyRepository.deleteAll();
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
        TestTransaction.flagForCommit();
    }

    @After
    public void after() {
        policyRepository.deleteAll();
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
    }


        @Test
    public void getOne() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, 1L);
        assertThat(purposeRepository.count(), is(1L));
        assertThat(legalBasisRepository.count(), is(1L));
        assertThat(policyRepository.count(), is(1L));
        assertLegalBasis();
        assertPurpose();
        assertPolicy();
    }

    @Test
    public void getAll() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, 1L);
        createTestdata("Legal basis 2", "PUR2", "Purpose2", 2L);
        assertThat(purposeRepository.count(), is(2L));
        assertThat(legalBasisRepository.count(), is(2L));
        assertThat(policyRepository.count(), is(2L));
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, String purposeDescription, Long informationTypeId) {
        if (TestTransaction.isActive()) {
            TestTransaction.end();
        }
        TestTransaction.start();
        LegalBasis lb = legalBasisRepository.save(LegalBasis.builder().description(legalBasisDescription).build());

        Purpose purpose = new Purpose();
        purpose.setPurposeId(purposeCode);
        purpose.setDescription(purposeDescription);
        purposeRepository.save(purpose);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        Policy policy = new Policy();
        policy.setInformationTypeId(informationTypeId);
        policy.setLegalBasis(lb);
        policy.setPurpose(purpose);
        policyRepository.save(policy);
    }

    private void assertLegalBasis() {
        LegalBasis legalBasis = legalBasisRepository.findAll().get(0);
        assertThat(legalBasis.getDescription(), is(LEGAL_BASIS_DESCRIPTION1));
    }

    private void assertPurpose() {
        Purpose purpose = purposeRepository.findAll().get(0);
        assertThat(purpose.getPurposeId(), is(PURPOSE_CODE1));
        assertThat(purpose.getDescription(), is(PURPOSE_DESCRIPTION1));
    }

    private void assertPolicy() {
        Policy policy = policyRepository.findAll().get(0);
        assertThat(policy.getLegalBasis().getDescription(), is(LEGAL_BASIS_DESCRIPTION1));
    }
}
