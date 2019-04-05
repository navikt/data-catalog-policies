package no.nav.data.catalog.policies.test.component.service;

import no.nav.data.catalog.policies.app.model.common.PolicyRequest;
import no.nav.data.catalog.policies.app.model.entities.LegalBasis;
import no.nav.data.catalog.policies.app.model.entities.Policy;
import no.nav.data.catalog.policies.app.model.entities.Purpose;
import no.nav.data.catalog.policies.app.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.repository.PolicyRepository;
import no.nav.data.catalog.policies.app.repository.PurposeRepository;
import no.nav.data.catalog.policies.app.service.PolicyService;
import no.nav.data.catalog.policies.test.component.ComponentTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.transaction.TestTransaction;

import javax.transaction.Transactional;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
@Transactional
public class PolicyServiceTest {
    public static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    public static final String PURPOSE_CODE1 = "PUR1";
    public static final String PURPOSE_DESCRIPTION1 = "Purpose 1";

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private PurposeRepository purposeRepository;

    @Autowired
    private LegalBasisRepository legalBasisRepository;

    @Autowired
    private PolicyService policyService;

    private Pageable pageRequest;

    @Before
    public void setUp() {
        policyRepository.deleteAll();
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
    }

    @After
    public void cleanUp() {
        policyRepository.deleteAll();
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
    }

    @Test
    public void createPolicy() {
        createBasicTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1);
        assertThat(legalBasisRepository.count(), is(1L));
        LegalBasis storedLegalBasis = legalBasisRepository.findAll().get(0);

        assertThat(purposeRepository.count(), is(1L));
        Purpose storedPurpose = purposeRepository.findAll().get(0);

        policyService.createPolicy(new PolicyRequest(storedLegalBasis.getLegalBasisId(), storedPurpose.getPurposeId(), 1L));
        assertThat(policyRepository.count(), is(1L));
        assertThat(policyRepository.findAll().get(0).getLegalBasis(), is(storedLegalBasis));
        assertThat(policyRepository.findAll().get(0).getPurpose(), is(storedPurpose));
        assertThat(policyRepository.findAll().get(0).getInformationTypeId(), is(1L));
    }

    @Test
    public void getAllPolicies() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, 100);

        pageRequest = new PageRequest(0, 100);
        Page<Policy> policies = policyService.getPolicies(pageRequest);
        assertThat(policyRepository.count(), is(100L));
        assertThat(purposeRepository.count(), is(100L));
        assertThat(legalBasisRepository.count(), is(100L));
        assertThat(policies.getTotalElements(), is(100L));
        assertThat(policies.getTotalPages(), is(1));
    }

    @Test
    public void get50FirstPolicies() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, 100);

        pageRequest = new PageRequest(0, 50);
        Page<Policy> policies = policyService.getPolicies(pageRequest);
        assertThat(policyRepository.count(), is(100L));
        assertThat(purposeRepository.count(), is(100L));
        assertThat(legalBasisRepository.count(), is(100L));
        assertThat(policies.getTotalElements(), is(100L));
        assertThat(policies.getTotalPages(), is(2));
    }

    @Test
    public void getPolicy() {
        createBasicTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1);
        assertThat(legalBasisRepository.count(), is(1L));
        LegalBasis storedLegalBasis = legalBasisRepository.findAll().get(0);

        assertThat(purposeRepository.count(), is(1L));
        Purpose storedPurpose = purposeRepository.findAll().get(0);

        Policy storedPolicy = policyService.createPolicy(new PolicyRequest(storedLegalBasis.getLegalBasisId(), storedPurpose.getPurposeId(), 1L));
        assertThat(policyRepository.count(), is(1L));

        Policy policy = policyService.getPolicy(storedPolicy.getPolicyId());
        assertThat(policy.getPurpose().getDescription(), is(PURPOSE_DESCRIPTION1));
        assertThat(policy.getPurpose().getPurposeCode(), is(PURPOSE_CODE1));
        assertThat(policy.getLegalBasis().getDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policy.getInformationTypeId(), is(1L));
    }

    @Test
    public void updatePolicy() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, 1);
        Policy storedPolicy = policyRepository.findAll().get(0);

        Policy originalPolicy = policyService.getPolicy(storedPolicy.getPolicyId());
        createBasicTestdata(LEGAL_BASIS_DESCRIPTION1 + "UPDATED", PURPOSE_CODE1 + "UPD", PURPOSE_DESCRIPTION1 + "UPDATED");
        assertThat(legalBasisRepository.count(), is(2L));
        LegalBasis storedLegalBasis = legalBasisRepository.findAll().get(1);

        assertThat(purposeRepository.count(), is(2L));
        Purpose storedPurpose = purposeRepository.findAll().get(1);

        Policy updatedPolicy = policyService.updatePolicy(originalPolicy.getPolicyId(), new PolicyRequest(storedLegalBasis.getLegalBasisId(), storedPurpose.getPurposeId(), 2L));

        assertThat(updatedPolicy.getPurpose().getDescription(), is(PURPOSE_DESCRIPTION1 + "UPDATED"));
        assertThat(updatedPolicy.getPurpose().getPurposeCode(), is(PURPOSE_CODE1 + "UPD"));
        assertThat(updatedPolicy.getLegalBasis().getDescription(), is(LEGAL_BASIS_DESCRIPTION1 + "UPDATED"));
        assertThat(updatedPolicy.getInformationTypeId(), is(2L));
    }

    @Test
    public void deletePolicy() {
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, 1);
        assertThat(policyRepository.count(), is(1L));

        policyService.deletePolicy(policyRepository.findAll().get(0).getPolicyId());
        assertThat(policyRepository.count(), is(0L));
    }


    @Test
    public void getLegalBasis() {
        createBasicTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1);
        List<LegalBasis> legalBasisList = policyService.getLegalBasis();
        assertThat(legalBasisList.size(), is(1));
        assertThat(legalBasisList.get(0).getDescription(), is(LEGAL_BASIS_DESCRIPTION1));
    }

    @Test
    public void getPurpose() {
        createBasicTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1);
        List<Purpose> purposeList = policyService.getPurposes();
        assertThat(purposeList.size(), is(1));
        assertThat(purposeList.get(0).getPurposeCode(), is(PURPOSE_CODE1));
        assertThat(purposeList.get(0).getDescription(), is(PURPOSE_DESCRIPTION1));
    }

    private void createBasicTestdata(String legalBasisDescription, String purposeCode, String purposeDescription) {
        legalBasisRepository.save(LegalBasis.builder().description(legalBasisDescription).build());
        Purpose purpose = new Purpose();
        purpose.setPurposeCode(purposeCode);
        purpose.setDescription(purposeDescription);
        purposeRepository.save(purpose);
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, String purposeDescription, int rows) {
        int i = 0;
        while (i < rows ){
            if (TestTransaction.isActive()) {
                TestTransaction.end();
            }
            TestTransaction.start();
            LegalBasis lb = legalBasisRepository.save(LegalBasis.builder().description(legalBasisDescription).build());

            Purpose purpose = new Purpose();
            purpose.setPurposeCode(purposeCode + i);
            purpose.setDescription(purposeDescription);
            purposeRepository.save(purpose);
            TestTransaction.flagForCommit();
            TestTransaction.end();

            Policy policy = new Policy();
            policy.setInformationTypeId(new Long(i));
            policy.setLegalBasis(lb);
            policy.setPurpose(purpose);
            policyRepository.save(policy);
            i++;
        }
    }
}
