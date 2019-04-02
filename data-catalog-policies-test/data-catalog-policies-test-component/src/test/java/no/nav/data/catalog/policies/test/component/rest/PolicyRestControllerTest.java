package no.nav.data.catalog.policies.test.component.rest;

import no.nav.data.catalog.policies.app.model.LegalBasis;
import no.nav.data.catalog.policies.app.model.Policy;
import no.nav.data.catalog.policies.app.model.Purpose;
import no.nav.data.catalog.policies.app.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.repository.PolicyRepository;
import no.nav.data.catalog.policies.app.repository.PurposeRepository;
import no.nav.data.catalog.policies.app.rest.PolicyRestController;
import no.nav.data.catalog.policies.test.component.ComponentTestConfig;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import javax.transaction.Transactional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
@Transactional
public class PolicyRestControllerTest {
    private static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    private static final String PURPOSE_CODE1 = "PUR1";
    private static final String PURPOSE_DESCRIPTION1 = "Purpose 1";

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private PurposeRepository purposeRepository;

    @Autowired
    private LegalBasisRepository legalBasisRepository;

    @Autowired
    private PolicyRestController restController;

    @Before
    public void setUp() {
        policyRepository.deleteAll();
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
        createTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1);
    }

    @Test
    public void createPolicy() {
        assertThat(legalBasisRepository.count(), is(1L));
        LegalBasis storedLegalBasis = legalBasisRepository.findAll().get(0);

        assertThat(purposeRepository.count(), is(1L));
        Purpose storedPurpose = purposeRepository.findAll().get(0);

        restController.createPolicy(new Policy(1L, 1L, storedPurpose, storedLegalBasis));
        assertThat(policyRepository.count(), is(1L));
        assertThat(policyRepository.findAll().get(0).getLegalBasis(), is(storedLegalBasis));
        assertThat(policyRepository.findAll().get(0).getPurpose(), is(storedPurpose));
        assertThat(policyRepository.findAll().get(0).getInformationTypeId(), is(1L));
    }

    private void createTestdata(String legalBasisDescription, String purposeCode, String purposeDescription) {
        legalBasisRepository.save(LegalBasis.builder().description(legalBasisDescription).build());

        Purpose purpose = new Purpose();
        purpose.setPurposeId(purposeCode);
        purpose.setDescription(purposeDescription);
        purposeRepository.save(purpose);
    }
}
