package no.nav.data.catalog.policies.test.component.mapper;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.policy.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.mapper.PolicyMapper;
import no.nav.data.catalog.policies.app.policy.repository.InformationTypeRepository;
import no.nav.data.catalog.policies.test.component.ComponentTestConfig;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Optional;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
public class PolicyMapperTest {
    @Mock
    private CodelistConsumer consumer;
    @Mock
    private InformationTypeRepository informationTypeRepository;
    @InjectMocks
    private PolicyMapper mapper;

    public static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    public static final String PURPOSE_CODE1 = "PUR1";
    public static final String PURPOSE_DESCRIPTION1 = "PurposeDescription 1";
    public static final String INFORMATION_TYPE_DESCRIPTION1 = "InformationTypeDescription 1";
    public static final String INFORMATION_TYPE_NAME1 = "InformationTypeNeme 1";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void shouldMapToPolicy() {
        InformationType informationType = createBasicTestdata(INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
        when(informationTypeRepository.findByName(anyString())).thenReturn(Optional.of(informationType));
        PolicyRequest request = new PolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, informationType.getName());
        Policy policy = mapper.mapRequestToPolicy(request, null);
        assertThat(policy.getInformationType().getName(), is(request.getInformationTypeName()));
        assertThat(policy.getLegalBasisDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policy.getPurposeCode(), is(PURPOSE_CODE1));
        assertThat(policy.getInformationType().getDescription(), is(INFORMATION_TYPE_DESCRIPTION1));
    }

    @Test
    public void shouldThrowExceptionWhenInformationTypeNotFound() {
        when(informationTypeRepository.findByName(anyString())).thenReturn(Optional.ofNullable(null));
        expectedException.expect(DataCatalogPoliciesNotFoundException.class);
        expectedException.expectMessage("Cannot find InformationType with name: NOTFOUND");
        PolicyRequest request = new PolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1,"NOTFOUND");
        mapper.mapRequestToPolicy(request, null);
    }

    @Test
    public void shouldMapToPolicyResonse() {
        when(consumer.getPurposeCodelistDescription(anyString())).thenReturn(PURPOSE_DESCRIPTION1);
        InformationType informationType = createBasicTestdata(INFORMATION_TYPE_DESCRIPTION1, INFORMATION_TYPE_NAME1);
        Policy policy = new Policy(1L, informationType, PURPOSE_CODE1, LEGAL_BASIS_DESCRIPTION1);
        PolicyResponse policyResponse = mapper.mapPolicyToRequest(policy);
        assertThat(policyResponse.getInformationType(), is(policy.getInformationType()));
        assertThat(policyResponse.getLegalBasisDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policyResponse.getPurpose().get("code"), is(PURPOSE_CODE1));
        assertThat(policyResponse.getPurpose().get("description"), is(PURPOSE_DESCRIPTION1));
    }

    private InformationType createBasicTestdata(String informationTypeDescription, String informationTypeName) {
         return InformationType.builder().informationTypeId(1L).description(informationTypeDescription).name(informationTypeName).build();
    }
}
