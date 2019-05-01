package no.nav.data.catalog.policies.test.component.mapper;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.policy.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import no.nav.data.catalog.policies.app.policy.entities.LegalBasis;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.entities.Purpose;
import no.nav.data.catalog.policies.app.policy.repository.InformationTypeRepository;
import no.nav.data.catalog.policies.app.policy.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.policy.repository.PurposeRepository;
import no.nav.data.catalog.policies.app.policy.mapper.PolicyMapper;
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
    @Autowired
    private InformationTypeRepository informationTypeRepository;

    public static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    public static final String PURPOSE_CODE1 = "PUR1";
    public static final String PURPOSE_DESCRIPTION1 = "Purpose 1";
    public static final String INFORMATION_TYPE_DESCRIPTION1 = "InformationTypeDescription 1";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
        informationTypeRepository.deleteAll();
        createBasicTestdata(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, PURPOSE_DESCRIPTION1, INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
    }

    @After
    public void cleanUp() {
        purposeRepository.deleteAll();
        legalBasisRepository.deleteAll();
        informationTypeRepository.deleteAll();
    }

    @Test
    public void shouldMap() {
        assertThat(legalBasisRepository.count(), is(1L));
        LegalBasis legalBasis = legalBasisRepository.findAll().get(0);
        assertThat(purposeRepository.count(), is(1L));
        Purpose purpose = purposeRepository.findAll().get(0);
        assertThat(informationTypeRepository.count(), is(1L));
        InformationType informationType = informationTypeRepository.findAll().get(0);
        PolicyRequest request = new PolicyRequest(legalBasis.getLegalBasisId(), LEGAL_BASIS_DESCRIPTION1, purpose.getPurposeId(), informationType.getInformationTypeId());
        Policy policy = mapper.mapRequestToPolicy(request, null);
        assertThat(policy.getInformationType().getInformationTypeId(), is(request.getInformationTypeId()));
        assertThat(policy.getLegalBasisDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policy.getLegalBasis().getDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policy.getPurpose().getDescription(), is(PURPOSE_DESCRIPTION1));
        assertThat(policy.getInformationType().getDescription(), is(INFORMATION_TYPE_DESCRIPTION1));
    }

    @Test
    public void shouldThrowExceptionWhenLegalBasisNotFound() {
        expectedException.expect(DataCatalogPoliciesNotFoundException.class);
        expectedException.expectMessage("Cannot find Legal basis with id: 666");
        assertThat(legalBasisRepository.count(), is(1L));
        assertThat(purposeRepository.count(), is(1L));
        Purpose purpose = purposeRepository.findAll().get(0);
        assertThat(informationTypeRepository.count(), is(1L));
        InformationType informationType = informationTypeRepository.findAll().get(0);
        PolicyRequest request = new PolicyRequest(666L, LEGAL_BASIS_DESCRIPTION1, purpose.getPurposeId(), informationType.getInformationTypeId());
        mapper.mapRequestToPolicy(request, null);
    }

    @Test
    public void shouldThrowExceptionWhenPurposeNotFound() {
        expectedException.expect(DataCatalogPoliciesNotFoundException.class);
        expectedException.expectMessage("Cannot find Purpose with id: 666");
        assertThat(legalBasisRepository.count(), is(1L));
        LegalBasis legalBasis = legalBasisRepository.findAll().get(0);
        assertThat(informationTypeRepository.count(), is(1L));
        InformationType informationType = informationTypeRepository.findAll().get(0);
        PolicyRequest request = new PolicyRequest(legalBasis.getLegalBasisId(), LEGAL_BASIS_DESCRIPTION1, 666L, informationType.getInformationTypeId());
        mapper.mapRequestToPolicy(request, null);
    }

    @Test
    public void shouldThrowExceptionWhenInformationTypeNotFound() {
        expectedException.expect(DataCatalogPoliciesNotFoundException.class);
        expectedException.expectMessage("Cannot find InformationType with id: 666");
        assertThat(legalBasisRepository.count(), is(1L));
        LegalBasis legalBasis = legalBasisRepository.findAll().get(0);
        assertThat(purposeRepository.count(), is(1L));
        Purpose purpose = purposeRepository.findAll().get(0);
        PolicyRequest request = new PolicyRequest(legalBasis.getLegalBasisId(), LEGAL_BASIS_DESCRIPTION1, purpose.getPurposeId(),666L);
        mapper.mapRequestToPolicy(request, null);
    }


    private void createBasicTestdata(String legalBasisDescription, String purposeCode, String purposeDescription, String informationTypeDescription, String informationTypeName) {
        legalBasisRepository.save(LegalBasis.builder().description(legalBasisDescription).build());
        Purpose purpose = new Purpose();
        purpose.setPurposeCode(purposeCode);
        purpose.setDescription(purposeDescription);
        purposeRepository.save(purpose);
        informationTypeRepository.save(InformationType.builder().informationTypeId(1L).description(informationTypeDescription).name(informationTypeName).build());
    }
}
