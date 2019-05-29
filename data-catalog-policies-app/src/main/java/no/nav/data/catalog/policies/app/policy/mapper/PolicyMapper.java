package no.nav.data.catalog.policies.app.policy.mapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.policy.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.InformationTypeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;

@Component
@Slf4j
public class PolicyMapper {
    private static final Logger logger = LoggerFactory.getLogger(PolicyMapper.class);

    @Autowired
    private InformationTypeRepository informationTypeRepository;

    @Autowired
    private CodelistConsumer consumer;

    public Policy mapRequestToPolicy(PolicyRequest policyRequest, Long id) {
        Optional<InformationType> optionalInformationType = informationTypeRepository.findByName(policyRequest.getInformationTypeName());
        if (!optionalInformationType.isPresent()) {
            logger.error(String.format("Cannot find InformationType with name: %s", policyRequest.getInformationTypeName()));
            throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find InformationType with name: %s", policyRequest.getInformationTypeName()));
        }
        InformationType informationType = optionalInformationType.get();

        Policy policy = new Policy();
        policy.setInformationType(informationType);
        policy.setPurposeCode(policyRequest.getPurposeCode());
        policy.setLegalBasisDescription(policyRequest.getLegalBasisDescription());
        if (id != null) {
            policy.setPolicyId(id);
        }
        return policy;
    }

    public PolicyResponse mapPolicyToRequest(Policy policy) {
        PolicyResponse response = new PolicyResponse();
        response.setPolicyId(policy.getPolicyId());
        response.setLegalBasisDescription(policy.getLegalBasisDescription());
        response.setInformationType(policy.getInformationType());
        response.setPurpose(Map.of("code", policy.getPurposeCode(), "description", consumer.getPurposeCodelistDescription(policy.getPurposeCode())));
        return response;
    }
}
