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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = ComponentTestConfig.class)
@ActiveProfiles("test")
class PolicyServiceTest {

    private static final String DATASET_TITLE = "Personalia";
    private static final String LEGALBASISDESCRIPTION = "LegalBasis";
    private static final String PURPOSECODE = "AAP";
    private static final String DATASET_ID_1 = "cd7f037e-374e-4e68-b705-55b61966b2fc";

    @Mock
    private CodelistConsumer codelistConsumer;

    @Mock
    private DatasetConsumer datasetConsumer;

    @Mock
    private PolicyRepository policyRepository;

    @InjectMocks
    private PolicyService service;

    @Test
    void shouldValidateInsertRequest() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().id(DATASET_ID_1).build());
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(String.class), anyString())).thenReturn(false);
        service.validateRequests(List.of(request));
    }

    @Test
    void shouldThrowAllNullValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder().build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().id(DATASET_ID_1).build());
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(String.class), anyString())).thenReturn(false);
        try {
            service.validateRequests(List.of(request));
            fail();
        } catch (ValidationException e) {
            assertEquals(3, e.get().get("Request nr:1").size());
            assertEquals("datasetTitle cannot be null", e.get().get("Request nr:1").get("datasetTitle"));
            assertEquals("purposeCode cannot be null", e.get().get("Request nr:1").get("purposeCode"));
            assertEquals("legalBasisDescription cannot be null", e.get().get("Request nr:1").get("legalBasisDescription"));
        }
    }

    @Test
    void shouldThrowNotFoundValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(String.class), anyString())).thenReturn(false);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        try {
            service.validateRequests(List.of(request));
            fail();
        } catch (ValidationException e) {
            assertEquals(2, e.get().get(DATASET_TITLE + "/" + PURPOSECODE).size());
            assertEquals("The purposeCode AAP was not found in the PURPOSE codelist.", e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("purposeCode"));
            assertEquals("A dataset with title " + DATASET_TITLE + " does not exist", e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("datasetTitle"));
        }
    }

    @Test
    void shouldThrowAlreadyExistsValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().id(DATASET_ID_1).build());
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(String.class), anyString())).thenReturn(true);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn("purpose");
        try {
            service.validateRequests(List.of(request));
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.get().get(DATASET_TITLE + "/" + PURPOSECODE).size());
            assertEquals("A policy combining Dataset Personalia and Purpose AAP already exists", e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("datasetAndPurpose"));
        }
    }

    @Test
    void shouldThrowAllNullValidationExceptionOnUpdate() {
        PolicyRequest request = PolicyRequest.builder().build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().id(DATASET_ID_1).build());
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(String.class), anyString())).thenReturn(false);
        try {
            service.validateRequests(List.of(request));
            fail();
        } catch (ValidationException e) {
            assertEquals(3, e.get().get("Request nr:1").size());
            assertEquals("datasetTitle cannot be null", e.get().get("Request nr:1").get("datasetTitle"));
            assertEquals("purposeCode cannot be null", e.get().get("Request nr:1").get("purposeCode"));
            assertEquals("legalBasisDescription cannot be null", e.get().get("Request nr:1").get("legalBasisDescription"));
        }
    }

    @Test
    void shouldThrowNotFoundValidationExceptionOnUpdate() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(String.class), anyString())).thenReturn(false);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        try {
            service.validateRequests(List.of(request));
            fail();
        } catch (ValidationException e) {
            assertEquals(2, e.get().get(DATASET_TITLE + "/" + PURPOSECODE).size());
            assertEquals("The purposeCode AAP was not found in the PURPOSE codelist.", e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("purposeCode"));
            assertEquals("A dataset with title " + DATASET_TITLE + " does not exist", e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("datasetTitle"));
        }
    }

    @Test
    void shouldNOTThrowAlreadyExistsValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .id(1L)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().id(DATASET_ID_1).build());
        when(policyRepository.existsByDatasetIdAndPurposeCode(any(String.class), anyString())).thenReturn(true);
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn("purpose");
        service.validateRequests(List.of(request));
    }
}
