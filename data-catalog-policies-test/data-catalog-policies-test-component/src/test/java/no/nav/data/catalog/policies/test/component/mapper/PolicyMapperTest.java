package no.nav.data.catalog.policies.test.component.mapper;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.policy.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.mapper.PolicyMapper;
import no.nav.data.catalog.policies.app.policy.repository.InformationTypeRepository;
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
    private InformationTypeRepository informationTypeRepository;

    public static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    public static final String PURPOSE_CODE1 = "PUR1";
    public static final String INFORMATION_TYPE_DESCRIPTION1 = "InformationTypeDescription 1";
    public static final String INFORMATION_TYPE_NAME1 = "InformationTypeNeme 1";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() {
        informationTypeRepository.deleteAll();
        createBasicTestdata(INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
    }

    @After
    public void cleanUp() {
        informationTypeRepository.deleteAll();
    }

    @Test
    public void shouldMap() {
        assertThat(informationTypeRepository.count(), is(1L));
        InformationType informationType = informationTypeRepository.findAll().get(0);
        PolicyRequest request = new PolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, informationType.getName());
        Policy policy = mapper.mapRequestToPolicy(request, null);
        assertThat(policy.getInformationType().getName(), is(request.getInformationTypeName()));
        assertThat(policy.getLegalBasisDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policy.getPurposeCode(), is(PURPOSE_CODE1));
        assertThat(policy.getInformationType().getDescription(), is(INFORMATION_TYPE_DESCRIPTION1));
    }

    @Test
    public void shouldThrowExceptionWhenInformationTypeNotFound() {
        expectedException.expect(DataCatalogPoliciesNotFoundException.class);
        expectedException.expectMessage("Cannot find InformationType with name: NOTFOUND");
        PolicyRequest request = new PolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1,"NOTFOUND");
        mapper.mapRequestToPolicy(request, null);
    }

    private void createBasicTestdata(String informationTypeDescription, String informationTypeName) {
        informationTypeRepository.save(InformationType.builder().informationTypeId(1L).description(informationTypeDescription).name(informationTypeName).build());
    }
}
