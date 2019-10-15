package no.nav.data.catalog.policies.test.component;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.ValidationException;
import no.nav.data.catalog.policies.app.common.util.JsonUtils;
import no.nav.data.catalog.policies.app.codelist.CodelistConsumer;
import no.nav.data.catalog.policies.app.dataset.DatasetConsumer;
import no.nav.data.catalog.policies.app.policy.PolicyService;
import no.nav.data.catalog.policies.app.dataset.domain.Dataset;
import no.nav.data.catalog.policies.app.codelist.domain.ListName;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
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
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().datasetId(DATASET_ID_1).build());
        when(policyRepository.findByDatasetIdAndPurposeCode(any(String.class), anyString())).thenReturn(List.of());
        service.validateRequests(List.of(request), false);
    }

    @Test
    void shouldThrowAllNullValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder().build();
        try {
            service.validateRequests(List.of(request), false);
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
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        try {
            service.validateRequests(List.of(request), false);
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
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().datasetId(DATASET_ID_1).build());
        when(policyRepository.findByDatasetIdAndPurposeCode(any(String.class), anyString()))
                .thenReturn(List.of(Policy.builder().fom(LocalDate.now()).tom(LocalDate.now()).build()));
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn("purpose");
        try {
            service.validateRequests(List.of(request), false);
            fail();
        } catch (ValidationException e) {
            assertEquals(1, e.get().get(DATASET_TITLE + "/" + PURPOSECODE).size());
            assertEquals("A policy combining Dataset Personalia and Purpose AAP already exists", e.get().get(DATASET_TITLE + "/" + PURPOSECODE).get("datasetAndPurpose"));
        }
    }

    @Test
    void shouldThrowAllNullValidationExceptionOnUpdate() {
        PolicyRequest request = PolicyRequest.builder().build();
        try {
            service.validateRequests(List.of(request), true);
            fail();
        } catch (ValidationException e) {
            assertEquals(4, e.get().get("Request nr:1").size());
            assertEquals("Id is missing for update", e.get().get("Request nr:1").get("missingIdForUpdate"));
            assertEquals("datasetTitle cannot be null", e.get().get("Request nr:1").get("datasetTitle"));
            assertEquals("purposeCode cannot be null", e.get().get("Request nr:1").get("purposeCode"));
            assertEquals("legalBasisDescription cannot be null", e.get().get("Request nr:1").get("legalBasisDescription"));
        }
    }

    @Test
    void shouldThrowNotFoundValidationExceptionOnUpdate() {
        PolicyRequest request = PolicyRequest.builder()
                .id(152L)
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenThrow(new DataCatalogPoliciesNotFoundException(""));
        when(policyRepository.findById(152L)).thenReturn(Optional.of(Policy.builder().purposeCode(PURPOSECODE).build()));
        try {
            service.validateRequests(List.of(request), true);
            fail();
        } catch (ValidationException e) {
            Map<String, String> map = e.get().get(DATASET_TITLE + "/" + PURPOSECODE);
            assertEquals(2, map.size(), JsonUtils.toJson(map));
            assertEquals("The purposeCode AAP was not found in the PURPOSE codelist.", map.get("purposeCode"));
            assertEquals("A dataset with title " + DATASET_TITLE + " does not exist", map.get("datasetTitle"));
        }
    }

    @Test
    void shouldThrowWrongPurposecodeOnUpdate() {
        PolicyRequest request = PolicyRequest.builder()
                .id(152L)
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .build();
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn("purpose");
        when(policyRepository.findById(152L)).thenReturn(Optional.of(Policy.builder().purposeCode("otherpurpose").build()));
        try {
            service.validateRequests(List.of(request), true);
            fail();
        } catch (ValidationException e) {
            Map<String, String> map = e.get().get(DATASET_TITLE + "/" + PURPOSECODE);
            assertEquals(1, map.size(), JsonUtils.toJson(map));
            assertEquals("Cannot change purpose from otherpurpose to AAP for policy 152", map.get("cannotChangePurpose"));
        }
    }

    @Test
    void shouldNotThrowAlreadyExistsValidationExceptionOnInsert() {
        PolicyRequest request = PolicyRequest.builder()
                .datasetTitle(DATASET_TITLE)
                .legalBasisDescription(LEGALBASISDESCRIPTION)
                .purposeCode(PURPOSECODE)
                .id(1L)
                .build();
        when(datasetConsumer.getDatasetByTitle(request.getDatasetTitle())).thenReturn(Dataset.builder().datasetId(DATASET_ID_1).build());
        when(codelistConsumer.getCodelistDescription(any(ListName.class), anyString())).thenReturn("purpose");
        service.validateRequests(List.of(request), false);
    }
}
