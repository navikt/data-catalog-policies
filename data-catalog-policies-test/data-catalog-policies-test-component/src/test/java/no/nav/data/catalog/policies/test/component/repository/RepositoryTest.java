package no.nav.data.catalog.policies.test.component.repository;

import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import no.nav.data.catalog.policies.app.policy.entities.LegalBasis;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.entities.Purpose;
import no.nav.data.catalog.policies.app.policy.repository.InformationTypeRepository;
import no.nav.data.catalog.policies.app.policy.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.app.policy.repository.PurposeRepository;
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
    private PurposeRepository purposeRepository;

    @Autowired
    private LegalBasisRepository legalBasisRepository;

    @Autowired
    private InformationTypeRepository informationTypeRepository;

    @Before
    public void setUp() {
        policyRepository.deleteAll();
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
        informationTypeRepository.deleteAll();
        TestTransaction.flagForCommit();
    }

    @After
    public void cleanUp() {
        policyRepository.deleteAll();
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
        informationTypeRepository.deleteAll();
    }


        @Test
    public void getOne() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, 1l, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
        assertThat(purposeRepository.count(), is(1L));
        assertThat(legalBasisRepository.count(), is(1L));
        assertThat(policyRepository.count(), is(1L));
        assertLegalBasis();
        assertPurpose();
        assertInformationType();
    }

    @Test
    public void getAll() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, 1L, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
        createTestdata("Legal basis 2", "PUR2", "Purpose2", 2L, INFORMATION_TYPE_DESCRIPTION1, "InformationTypeName2");
        assertThat(purposeRepository.count(), is(2L));
        assertThat(legalBasisRepository.count(), is(2L));
        assertThat(policyRepository.count(), is(2L));
        assertThat(informationTypeRepository.count(), is(2L));
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, String purposeDescription, Long informationTypeId, String informationTypeDescription, String informationTypeName) {
        if (TestTransaction.isActive()) {
            TestTransaction.end();
        }
        TestTransaction.start();
        LegalBasis lb = legalBasisRepository.save(LegalBasis.builder().description(legalBasisDescription).build());

        Purpose purpose = new Purpose();
        purpose.setPurposeCode(purposeCode);
        purpose.setDescription(purposeDescription);
        purposeRepository.save(purpose);
        TestTransaction.flagForCommit();
        TestTransaction.end();

        InformationType informationType = informationTypeRepository.save(InformationType.builder().informationTypeId(informationTypeId).description(informationTypeDescription).name(informationTypeName).build());

        Policy policy = new Policy();
        policy.setInformationType(informationType);
        policy.setLegalBasis(lb);
        policy.setPurpose(purpose);
        policyRepository.save(policy);
    }

    private void assertLegalBasis() {
        LegalBasis legalBasis = legalBasisRepository.findAll().get(0);
        assertThat(legalBasis.getDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertNotNull(legalBasis.getCreatedDate());
        assertNotNull(legalBasis.getLastModifiedDate());
        assertThat(legalBasis.getCreatedBy(), is("Datajeger"));
        assertThat(legalBasis.getLastModifiedBy(), is("Datajeger"));
    }

    private void assertPurpose() {
        Purpose purpose = purposeRepository.findAll().get(0);
        assertThat(purpose.getPurposeCode(), is(PURPOSE_CODE1));
        assertThat(purpose.getDescription(), is(PURPOSE_DESCRIPTION1));
        assertNotNull(purpose.getCreatedDate());
        assertNotNull(purpose.getLastModifiedDate());
        assertThat(purpose.getCreatedBy(), is("Datajeger"));
        assertThat(purpose.getLastModifiedBy(), is("Datajeger"));
    }

    private void assertInformationType() {
        InformationType informationType =informationTypeRepository.findAll().get(0);
        assertThat(informationType.getDescription(), is(INFORMATION_TYPE_DESCRIPTION1));
        assertNotNull(informationType.getCreatedDate());
        assertNotNull(informationType.getLastModifiedDate());
        assertThat(informationType.getCreatedBy(), is("Datajeger"));
        assertThat(informationType.getLastModifiedBy(), is("Datajeger"));
    }

}
