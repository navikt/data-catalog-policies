package no.nav.data.catalog.policies.test.component;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.ValidationException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.consumer.DatasetConsumer;
import no.nav.data.catalog.policies.app.policy.PolicyService;
import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
public class PolicyServiceTest {

    private static final String DATASET_TITLE = "Personalia";
    private static final String LEGALBASISDESCRIPTION = "LegalBasis";
    private static final String PURPOSECODE = "AAP";
    private static final UUID DATASET_ID_1 = UUID.fromString("cd7f037e-374e-4e68-b705-55b61966b2fc");

    @Mock
    private CodelistConsumer codelistConsumer;

    @Mock
    private DatasetConsumer datasetConsumer;

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService service;

    @ClassRule
    public static PolicyTestContainer postgreSQLContainer = PolicyTestContainer.getInstance();

    @Test
    public void shouldValidateInsertRequest() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().datasetId(DATASET_ID_1).build());
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(UUID.class), anyString())).thenReturn(false);
        service.validateRequests(List.of(request));
    }

    @Test
    public void shouldThrowAllNullValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().datasetId(DATASET_ID_1).build());
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(UUID.class), anyString())).thenReturn(false);
        try {
            service.validateRequests(List.of(request));
        } catch (ValidationException e) {
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).size(), is(3));
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("datasetTitle"), is("datasetTitle cannot be null"));
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("purposeCode"), is("purposeCode cannot be null"));
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("legalBasisDescription"), is("legalBasisDescription cannot be null"));
        }
    }

    @Test
    public void shouldThrowNotFoundValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(UUID.class), anyString())).thenReturn(false);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        try {
            service.validateRequests(List.of(request));
        } catch (ValidationException e) {
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).size(), is(2));
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("purposeCode"), is("The purposeCode AAP was not found in the PURPOSE codelist."));
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("datasetTitle"), is("A dataset with title " + DATASET_TITLE + " does not exist"));
        }
    }

    @Test
    public void shouldThrowAlreadyExistsValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().datasetId(DATASET_ID_1).build());
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(UUID.class), anyString())).thenReturn(true);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn("purpose");
        try {
            service.validateRequests(List.of(request));
        } catch (ValidationException e) {
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).size(), is(1));
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("datasetAndPurpose"), is("A policy combining Dataset Personalia and Purpose AAP already exists"));
        }
    }

    @Test
    public void shouldThrowAllNullValidationExceptionOnUpdate() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().datasetId(DATASET_ID_1).build());
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(UUID.class), anyString())).thenReturn(false);
        try {
            service.validateRequests(List.of(request));
        } catch (ValidationException e) {
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).size(), is(3));
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("datasetTitle"), is("datasetTitle cannot be null"));
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("purposeCode"), is("purposeCode cannot be null"));
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("legalBasisDescription"), is("legalBasisDescription cannot be null"));
        }
    }

    @Test
    public void shouldThrowNotFoundValidationExceptionOnUpdate() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(UUID.class), anyString())).thenReturn(false);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        try {
            service.validateRequests(List.of(request));
        } catch (ValidationException e) {
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).size(), is(2));
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("purposeCode"), is("The purposeCode AAP was not found in the PURPOSE codelist."));
            assertThat(e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("datasetTitle"), is("A dataset with title " + DATASET_TITLE + " does not exist"));
        }
    }

    @Test
    public void shouldNOTThrowAlreadyExistsValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .id(1L)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().datasetId(DATASET_ID_1).build());
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(UUID.class), anyString())).thenReturn(true);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn("purpose");
        service.validateRequests(List.of(request));
    }
}
