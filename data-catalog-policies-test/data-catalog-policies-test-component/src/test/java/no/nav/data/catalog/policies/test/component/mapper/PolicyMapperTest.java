package no.nav.data.catalog.policies.test.component.mapper;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.codelist.CodelistConsumer;
import no.nav.data.catalog.policies.app.dataset.domain.DatasetResponse;
import no.nav.data.catalog.policies.app.codelist.domain.ListName;
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
        DatasetResponse dataset = createBasicTestdata();
        PolicyRequest request = PolicyRequest.builder()
                .legalBasisDescription(LEGAL_BASIS_DESCRIPTION1).purposeCode(PURPOSE_CODE1)
                .datasetId(dataset.getId()).datasetTitle(dataset.getTitle()).build();
        Policy policy = mapper.mapRequestToPolicy(request, null);
        assertThat(policy.getLegalBasisDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policy.getPurposeCode(), is(PURPOSE_CODE1));
        assertThat(policy.getDatasetId(), is(dataset.getId()));
        assertThat(policy.getDatasetTitle(), is(dataset.getTitle()));
    }

    @Test
    void shouldMapToPolicyResponse() {
        DatasetResponse dataset = createBasicTestdata();
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn(PURPOSE_DESCRIPTION1);
        Policy policy = createPolicy(dataset);
        PolicyResponse policyResponse = mapper.mapPolicyToResponse(policy);
        assertThat(policyResponse.getDataset().getId(), is(policy.getDatasetId()));
        assertThat(policyResponse.getDataset().getTitle(), is(policy.getDatasetTitle()));
        assertThat(policyResponse.getLegalBasisDescription(), is(LEGAL_BASIS_DESCRIPTION1));
        assertThat(policyResponse.getPurpose().getCode(), is(PURPOSE_CODE1));
        assertThat(policyResponse.getPurpose().getDescription(), is(PURPOSE_DESCRIPTION1));
    }

    @Test
    void shouldThrowPurposeNotFoundExceptionResponse() {
        DatasetResponse dataset = createBasicTestdata();
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenThrow(new DataCatalogPoliciesNotFoundException("codelist not found"));
        Policy policy = createPolicy(dataset);
        Assertions.assertThrows(DataCatalogPoliciesNotFoundException.class, () -> mapper.mapPolicyToResponse(policy));
    }

    private Policy createPolicy(DatasetResponse dataset) {
        return Policy.builder().policyId(1L)
                .legalBasisDescription(LEGAL_BASIS_DESCRIPTION1).purposeCode(PURPOSE_CODE1)
                .datasetTitle(dataset.getTitle()).datasetId(dataset.getId())
                .build();
    }

    private DatasetResponse createBasicTestdata() {
        return DatasetResponse.builder()
                .id(DATASET_ID_1)
                .title(DATASET_TITLE_1)
                .build();
    }
}
