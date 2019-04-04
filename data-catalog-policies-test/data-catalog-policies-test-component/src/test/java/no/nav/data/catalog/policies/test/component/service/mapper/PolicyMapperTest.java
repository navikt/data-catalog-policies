package no.nav.data.catalog.policies.test.component.service.mapper;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesFunctionalException;
import no.nav.data.catalog.policies.app.model.common.PolicyRequest;
import no.nav.data.catalog.policies.app.model.entities.LegalBasis;
import no.nav.data.catalog.policies.app.model.entities.Policy;
import no.nav.data.catalog.policies.app.model.entities.Purpose;
import no.nav.data.catalog.policies.app.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.repository.PurposeRepository;
import no.nav.data.catalog.policies.app.service.mapper.PolicyMapper;
import no.nav.data.catalog.policies.test.component.ComponentTestConfig;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import static no.nav.data.catalog.policies.test.component.service.PolicyServiceTest.*;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
public class PolicyMapperTest {
    @Autowired
    private PolicyMapper mapper;
    @Autowired
    private PurposeRepository purposeRepository;
    @Autowired
    private LegalBasisRepository legalBasisRepository;

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
        createBasicTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1);
    }

    @After
    public void cleanUp() {
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
    }

    @Test
    public void shouldMap() {
        assertThat(legalBasisRepository.count(), is(1L));
        LegalBasis legalBasis = legalBasisRepository.findAll().get(0);
        assertThat(purposeRepository.count(), is(1L));
        Purpose purpose = purposeRepository.findAll().get(0);
        PolicyRequest request = new PolicyRequest(legalBasis.getLegalBasisId(), purpose.getPurposeId(), 1L);
        Policy policy = mapper.mapRequestToPolicy(request);
        assertThat(policy.getInformationTypeId(), is(request.getInformationTypeId()));
        assertThat(policy.getLegalBasis().getDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policy.getPurpose().getDescription(), is(PURPOSE_DESCRIPTION1));
    }

    @Test
    public void shouldThrowExceptionWhenLegalBasisNotFound() {
        expectedException.expect(DataCatalogPoliciesFunctionalException.class);
        expectedException.expectMessage("Cannot find Legal basis with id: 666");
        assertThat(legalBasisRepository.count(), is(1L));
        assertThat(purposeRepository.count(), is(1L));
        Purpose purpose = purposeRepository.findAll().get(0);
        PolicyRequest request = new PolicyRequest(666L, purpose.getPurposeId(), 1L);
        mapper.mapRequestToPolicy(request);
    }

    @Test
    public void shouldThrowExceptionWhenPurposeNotFound() {
        expectedException.expect(DataCatalogPoliciesFunctionalException.class);
        expectedException.expectMessage("Cannot find Purpose with id: 666");
        assertThat(legalBasisRepository.count(), is(1L));
        LegalBasis legalBasis = legalBasisRepository.findAll().get(0);
        PolicyRequest request = new PolicyRequest(legalBasis.getLegalBasisId(), 666L, 1L);
        mapper.mapRequestToPolicy(request);
    }

    private void createBasicTestdata(String legalBasisDescription, String purposeCode, String purposeDescription) {
        legalBasisRepository.save(LegalBasis.builder().description(legalBasisDescription).build());
        Purpose purpose = new Purpose();
        purpose.setPurposeCode(purposeCode);
        purpose.setDescription(purposeDescription);
        purposeRepository.save(purpose);
    }
}
