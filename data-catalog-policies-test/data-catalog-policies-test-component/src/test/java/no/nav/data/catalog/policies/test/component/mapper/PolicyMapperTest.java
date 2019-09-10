package no.nav.data.catalog.policies.test.component.mapper;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.consumer.DatasetConsumer;
import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.domain.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.mapper.PolicyMapper;
import no.nav.data.catalog.policies.test.component.ComponentTestConfig;
import no.nav.data.catalog.policies.test.component.PolicyTestContainer;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
public class PolicyMapperTest {

    @Mock
    private CodelistConsumer codelistConsumer;
    @Mock
    private DatasetConsumer datasetConsumer;
    @InjectMocks
    private PolicyMapper mapper;

    private static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    private static final String PURPOSE_CODE1 = "PUR1";
    private static final String PURPOSE_DESCRIPTION1 = "PurposeDescription 1";
    private static final String DATASET_TITLE_1 = "DatasetTitle 1";
    private static final UUID DATASET_ID_1 = UUID.fromString("cd7f037e-374e-4e68-b705-55b61966b2fc");

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @ClassRule
    public static PolicyTestContainer postgreSQLContainer = PolicyTestContainer.getInstance();

    @Test
    public void shouldMapToPolicy() {
        Dataset dataset = createBasicTestdata(DATASET_TITLE_1);
        when(datasetConsumer.getDatasetByTitle(DATASET_TITLE_1)).thenReturn(dataset);
        PolicyRequest request = new PolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, dataset.getTitle());
        request.setDatasetId(DATASET_ID_1);
        Policy policy = mapper.mapRequestToPolicy(request, null);
        assertThat(policy.getLegalBasisDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policy.getPurposeCode(), is(PURPOSE_CODE1));
        assertThat(policy.getDatasetId(), is(dataset.getId()));
    }

    @Test
    public void shouldMapToPolicyResponse() {
        Dataset dataset = createBasicTestdata(DATASET_TITLE_1);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn(PURPOSE_DESCRIPTION1);
        when(datasetConsumer.getDatasetById(DATASET_ID_1)).thenReturn(dataset);
        Policy policy = new Policy(1L, dataset.getId(), PURPOSE_CODE1, LEGAL_BASIS_DESCRIPTION1);
        PolicyResponse policyResponse = mapper.mapPolicyToResponse(policy);
        assertThat(policyResponse.getDataset().getId(), is(policy.getDatasetId()));
        assertThat(policyResponse.getLegalBasisDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policyResponse.getPurpose().getCode(), is(PURPOSE_CODE1));
        assertThat(policyResponse.getPurpose().getDescription(), is(PURPOSE_DESCRIPTION1));
    }

    @Test
    public void shouldThrowPurposeNotFoundExceptionResponse() {
        expectedException.expect(DataCatalogPoliciesNotFoundException.class);
        Dataset dataset = createBasicTestdata(DATASET_TITLE_1);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenThrow(new DataCatalogPoliciesNotFoundException("codelist not found"));
        Policy policy = new Policy(1L, dataset.getId(), PURPOSE_CODE1, LEGAL_BASIS_DESCRIPTION1);
        mapper.mapPolicyToResponse(policy);
    }

    private Dataset createBasicTestdata(String datasetTitle) {
        return Dataset.builder()
                .id(DATASET_ID_1)
                .title(datasetTitle)
                .build();
    }
}
