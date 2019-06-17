package no.nav.data.catalog.policies.app.policy;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.ValidationException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.consumer.InformationTypeConsumer;
import no.nav.data.catalog.policies.app.policy.domain.InformationType;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class PolicyService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyService.class);

    @Autowired
    private CodelistConsumer codelistConsumer;

    @Autowired
    private InformationTypeConsumer informationTypeConsumer;

    @Autowired
    private PolicyRepository policyRepository;


    public void validateRequests(List<PolicyRequest> requests) {
        HashMap<String, HashMap> validationMap = new HashMap<>();
        HashMap<String, Integer> namesUsedInRequest = new HashMap<>();
        final AtomicInteger i = new AtomicInteger(1);
        for (PolicyRequest request : requests) {
            HashMap<String, String> requestMap = validateRequest(request, isUpdate(request.getId()));
            if (!requestMap.isEmpty()) {
                validationMap.put(request.getInformationTypeName() + "/" + request.getPurposeCode(), requestMap);
            }
            if (namesUsedInRequest.containsKey(request.getInformationTypeName()+request.getPurposeCode())) {
                requestMap.put("combinationNotUniqueInThisRequest", String.format("A request combining InformationType: %s and Purpose: %s is not unique because " + "" +
                                "it is already used in this request (see request nr:%s)",
                        request.getInformationTypeName(), request.getPurposeCode(), namesUsedInRequest.get(request.getInformationTypeName()+request.getPurposeCode())));
            } else if (request.getInformationTypeName() != null && request.getPurposeCode() != null) {
                namesUsedInRequest.put(request.getInformationTypeName()+request.getPurposeCode(), i.intValue());
            }

            if (!requestMap.isEmpty()) {
                validationMap.put(String.format("Request nr:%s", i.intValue()), requestMap);
            }
            i.getAndIncrement();
        }
        if (!validationMap.isEmpty()) {
            logger.error("Validation errors occurred when validating InformationTypeRequest: {}", validationMap);
            throw new ValidationException(validationMap, "Validation errors occurred when validating InformationTypeRequest.");
        }
    }

    private boolean isUpdate(Long id) {
        return id != null;
    }

    private HashMap validateRequest(PolicyRequest request, boolean isUpdate) throws ValidationException {
        HashMap<String, String> validationErrors = new HashMap<>();
        if (request.getInformationTypeName() == null) {
            validationErrors.put("informationTypeName", "informationTypeName cannot be null");
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
        // Combination of InformationType and purpose must be unique
        if (request.getInformationTypeName() != null && request.getPurposeCode() != null) {
            InformationType informationType = null;
            try {
                informationType = informationTypeConsumer.getInformationTypeByName(request.getInformationTypeName());
            } catch (DataCatalogPoliciesNotFoundException e) {
                validationErrors.put("informationTypeName", String.format("An informationType with name %s does not exist", request.getInformationTypeName()));
            }
            if (informationType != null) {
                request.setInformationTypeId(informationType.getId());
            }

            if (!isUpdate && informationType != null && policyRepository.existsByInformationTypeIdAndPurposeCode(informationType.getId(), request.getPurposeCode())) {
                validationErrors.put("InformationTypeAndPurpose", String.format("A policy combining InformationType %s and Purpose %s already exists"
                        , request.getInformationTypeName(), request.getPurposeCode()));
            }
        }

        if (!validationErrors.isEmpty()) {
            logger.error("Validation errors occurred when validating InformationTypeRequest: {}", validationErrors);
        }
        return validationErrors;
    }

}
