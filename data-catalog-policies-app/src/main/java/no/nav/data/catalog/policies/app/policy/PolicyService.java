package no.nav.data.catalog.policies.app.policy;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.ValidationException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import no.nav.data.catalog.policies.app.policy.repository.InformationTypeRepository;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PolicyService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyService.class);

    @Autowired
    private CodelistConsumer consumer;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private InformationTypeRepository informationTypeRepository;

    public void validateRequests(List<PolicyRequest> requests) {
        HashMap<String, HashMap> validationMap = new HashMap<>();
        for (PolicyRequest request:requests) {
            HashMap<String, String> requestMap = validateRequest(request, isUpdate(request.getId()));
            if (!requestMap.isEmpty()) {
                validationMap.put(request.getInformationTypeName() + "/" + request.getPurposeCode(), requestMap);
            }
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
                consumer.getPurposeCodelistDescription(request.getPurposeCode());
            } catch (DataCatalogPoliciesNotFoundException e) {
                validationErrors.put("purposeCode", String.format("The purposeCode %s was not found in the PURPOSE codelist.", request.getPurposeCode()));
            }
        }
        // Combination of InformationType and purpose must be unique
        InformationType informationType = null;
        if (request.getInformationTypeName() != null) {
            Optional<InformationType> optionalInformationType = informationTypeRepository.findByName(request.getInformationTypeName());
            if (!optionalInformationType.isPresent()) {
                validationErrors.put("informationTypeName", String.format("An informationType with name %s does not exist", request.getInformationTypeName()));
            } else {
                informationType = optionalInformationType.get();
            }
        }

        if (!isUpdate && informationType != null && policyRepository.existsByInformationTypeInformationTypeIdAndPurposeCode(informationType.getInformationTypeId(), request.getPurposeCode())) {
            validationErrors.put("InformationTypeAndPurpose", String.format("A policy combining InformationType %s and Purpose %s already exists", request.getInformationTypeName(), request.getPurposeCode()));
        }

        if(!validationErrors.isEmpty()) {
            logger.error("Validation errors occurred when validating InformationTypeRequest: {}", validationErrors);
        }
        return validationErrors;
    }
}