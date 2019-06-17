package no.nav.data.catalog.policies.test.component.repository;

import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.InformationTypeRepository;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.test.component.ComponentTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    private InformationTypeRepository informationTypeRepository;

    @Before
    public void setUp() {
        policyRepository.deleteAll();
        informationTypeRepository.deleteAll();
        TestTransaction.flagForCommit();
    }

    @After
    public void cleanUp() {
        policyRepository.deleteAll();
        informationTypeRepository.deleteAll();
    }


    @Test
    public void getOne() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 1l, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
        assertThat(policyRepository.count(), is(1L));
        assertInformationType();
    }

    @Test
    public void getAll() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 1L, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
        createTestdata("Legal basis 2", "PUR2", 2L, INFORMATION_TYPE_DESCRIPTION1, "InformationTypeName2");
        assertThat(policyRepository.count(), is(2L));
        assertThat(informationTypeRepository.count(), is(2L));
    }

    @Test
    public void getByInformationType() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 1L, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
        createTestdata("Legal basis 2", "PUR2", 2L, INFORMATION_TYPE_DESCRIPTION1, "InformationTypeName2");
        assertThat(policyRepository.findByInformationTypeInformationTypeId(PageRequest.of(0, 10), 1L).getTotalElements(), is(1L));
        assertThat(policyRepository.findByInformationTypeInformationTypeId(PageRequest.of(0, 10), 2L).getTotalElements(), is(1L));
        assertThat(informationTypeRepository.count(), is(2L));
    }

    @Test
    public void countByInformationType() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, 1L, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
        createTestdata("Legal basis 2", "PUR2", 2L, INFORMATION_TYPE_DESCRIPTION1, "InformationTypeName2");
        assertThat(policyRepository.countByInformationTypeInformationTypeId(1L), is(1L));
        assertThat(policyRepository.countByInformationTypeInformationTypeId(2L), is(1L));
        assertThat(informationTypeRepository.count(), is(2L));
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, Long informationTypeId, String informationTypeDescription, String informationTypeName) {
        if (TestTransaction.isActive()) {
            TestTransaction.end();
        }

        InformationType informationType = informationTypeRepository.save(InformationType.builder().informationTypeId(informationTypeId).description(informationTypeDescription).name(informationTypeName).build());

        Policy policy = new Policy();
        policy.setInformationType(informationType);
        policy.setPurposeCode(purposeCode);
        policy.setLegalBasisDescription(legalBasisDescription);
        policyRepository.save(policy);
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
