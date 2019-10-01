package no.nav.data.catalog.policies.app.policy;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.ValidationException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.consumer.DatasetConsumer;
import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class PolicyService {

    private final CodelistConsumer codelistConsumer;
    private final DatasetConsumer datasetConsumer;
    private final PolicyRepository policyRepository;

    public PolicyService(CodelistConsumer codelistConsumer, DatasetConsumer datasetConsumer, PolicyRepository policyRepository) {
        this.codelistConsumer = codelistConsumer;
        this.datasetConsumer = datasetConsumer;
        this.policyRepository = policyRepository;
    }

    public void validateRequests(List<PolicyRequest> requests, boolean isUpdate) {
        Map<String, Map<String, String>> validationMap = new HashMap<>();
        Map<String, Integer> titlesUsedInRequest = new HashMap<>();
        final AtomicInteger i = new AtomicInteger(1);
        requests.forEach(request -> {
            Map<String, String> requestMap = validatePolicyRequest(request, isUpdate);
            if (titlesUsedInRequest.containsKey(request.getDatasetTitle() + request.getPurposeCode())) {
                requestMap.put("combinationNotUniqueInThisRequest", String.format("A request combining Dataset: %s and Purpose: %s is not unique because " + "" +
                                "it is already used in this request (see request nr:%s)",
                        request.getDatasetTitle(), request.getPurposeCode(), titlesUsedInRequest.get(request.getDatasetTitle() + request.getPurposeCode())));
            } else if (request.getDatasetTitle() != null && request.getPurposeCode() != null) {
                titlesUsedInRequest.put(request.getDatasetTitle() + request.getPurposeCode(), i.intValue());
            }
            if (isUpdate) {
                validateUpdate(request, requestMap);
            }
            if (!requestMap.isEmpty()) {
                validationMap.put(request.getDatasetTitle() + "/" + request.getPurposeCode(), requestMap);
                validationMap.put(String.format("Request nr:%s", i.intValue()), requestMap);
            }
            i.getAndIncrement();
        });
        if (!validationMap.isEmpty()) {
            log.error("Validation errors occurred when validating DatasetRequest: {}", validationMap);
            throw new ValidationException(validationMap, "Validation errors occurred when validating DatasetRequest.");
        }
    }

    private void validateUpdate(PolicyRequest request, Map<String, String> requestMap) {
        if (request.getId() == null) {
            requestMap.put("missingIdForUpdate", "Id is missing for update");
            return;
        }
        Policy policy = policyRepository.findById(request.getId()).orElse(null);
        if (policy == null) {
            requestMap.put("notFound", String.format("A policy with id: %d was not found", request.getId()));
        } else if (!StringUtils.equals(policy.getPurposeCode(), request.getPurposeCode())) {
            requestMap.put("cannotChangePurpose",
                    String.format("Cannot change purpose from %s to %s for policy %s", policy.getPurposeCode(), request.getPurposeCode(), request.getId()));
        } else {
            request.setExistingPolicy(policy);
        }
    }

    private Map<String, String> validatePolicyRequest(PolicyRequest request, boolean isUpdate) {
        Map<String, String> validationErrors = new HashMap<>();
        if (request.getDatasetTitle() == null) {
            validationErrors.put("datasetTitle", "datasetTitle cannot be null");
        }
        if (request.getLegalBasisDescription() == null) {
            validationErrors.put("legalBasisDescription", "legalBasisDescription cannot be null");
        }
        if (request.getPurposeCode() == null) {
            validationErrors.put("purposeCode", "purposeCode cannot be null");
        } else {
            try {
                codelistConsumer.getCodelistDescription(ListName.PURPOSE, request.getPurposeCode());
            } catch (DataCatalogPoliciesNotFoundException e) {
                validationErrors.put("purposeCode", String.format("The purposeCode %s was not found in the PURPOSE codelist.", request.getPurposeCode()));
            }
        }
        // Combination of Dataset and purpose must be unique
        if (request.getPurposeCode() != null && request.getDatasetTitle() != null) {
            Dataset dataset = null;
            try {
                dataset = datasetConsumer.getDatasetByTitle(request.getDatasetTitle());
            } catch (DataCatalogPoliciesNotFoundException e) {
                validationErrors.put("datasetTitle", String.format("A dataset with title %s does not exist", request.getDatasetTitle()));
            }
            if (dataset != null) {
                request.setDatasetId(dataset.getDatasetId());
            }

            if (!isUpdate && dataset != null && policyRepository.existsByDatasetIdAndPurposeCode(dataset.getDatasetId(), request.getPurposeCode())) {
                validationErrors.put("datasetAndPurpose",
                        String.format("A policy combining Dataset %s and Purpose %s already exists", request.getDatasetTitle(), request.getPurposeCode()));
            }
        }

        if (!validationErrors.isEmpty()) {
            log.error("Validation errors occurred when validating DatasetRequest: {}", validationErrors);
        }
        return validationErrors;
    }

}
