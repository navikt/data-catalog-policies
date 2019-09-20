package no.nav.data.catalog.policies.test.component.mapper;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.domain.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.mapper.PolicyMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class PolicyMapperTest {

    @Mock
    private CodelistConsumer codelistConsumer;
    @InjectMocks
    private PolicyMapper mapper;

    private static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    private static final String PURPOSE_CODE1 = "PUR1";
    private static final String PURPOSE_DESCRIPTION1 = "PurposeDescription 1";
    private static final String DATASET_TITLE_1 = "DatasetTitle 1";
    private static final String DATASET_ID_1 = "cd7f037e-374e-4e68-b705-55b61966b2fc";

    @Test
    void shouldMapToPolicy() {
        Dataset dataset = createBasicTestdata(DATASET_TITLE_1);
        PolicyRequest request = new PolicyRequest(LEGAL_BASIS_DESCRIPTION1, PURPOSE_CODE1, dataset.getTitle());
        request.setDatasetId(DATASET_ID_1);
        Policy policy = mapper.mapRequestToPolicy(request, null);
        assertThat(policy.getLegalBasisDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policy.getPurposeCode(), is(PURPOSE_CODE1));
        assertThat(policy.getDatasetId(), is(dataset.getId()));
        assertThat(policy.getDatasetTitle(), is(dataset.getTitle()));
    }

    @Test
    void shouldMapToPolicyResponse() {
        Dataset dataset = createBasicTestdata(DATASET_TITLE_1);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn(PURPOSE_DESCRIPTION1);
        Policy policy = new Policy(1L, dataset.getId(), dataset.getTitle(), PURPOSE_CODE1, LEGAL_BASIS_DESCRIPTION1);
        PolicyResponse policyResponse = mapper.mapPolicyToResponse(policy);
        assertThat(policyResponse.getDataset().getId(), is(policy.getDatasetId()));
        assertThat(policyResponse.getDataset().getTitle(), is(policy.getDatasetTitle()));
        assertThat(policyResponse.getLegalBasisDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policyResponse.getPurpose().getCode(), is(PURPOSE_CODE1));
        assertThat(policyResponse.getPurpose().getDescription(), is(PURPOSE_DESCRIPTION1));
    }

    @Test
    void shouldThrowPurposeNotFoundExceptionResponse() {
        Dataset dataset = createBasicTestdata(DATASET_TITLE_1);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenThrow(new DataCatalogPoliciesNotFoundException("codelist not found"));
        Policy policy = new Policy(1L, dataset.getId(), dataset.getTitle(), PURPOSE_CODE1, LEGAL_BASIS_DESCRIPTION1);
        Assertions.assertThrows(DataCatalogPoliciesNotFoundException.class, () -> mapper.mapPolicyToResponse(policy));
    }

    private Dataset createBasicTestdata(String datasetTitle) {
        return Dataset.builder()
                .id(DATASET_ID_1)
                .title(datasetTitle)
                .build();
    }
}
