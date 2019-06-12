package no.nav.data.catalog.policies.app.policy.mapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.consumer.InformationTypeConsumer;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import no.nav.data.catalog.policies.app.policy.domain.InformationType;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.domain.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
public class PolicyMapper {
    private static final Logger logger = LoggerFactory.getLogger(PolicyMapper.class);

    @Autowired
    private CodelistConsumer codelistConsumer;

    @Autowired
    private InformationTypeConsumer informationTypeConsumer;

    public Policy mapRequestToPolicy(PolicyRequest policyRequest, Long id) {
        Policy policy = new Policy();
        policy.setInformationTypeId(policyRequest.getInformationTypeId());
        policy.setPurposeCode(policyRequest.getPurposeCode());
        policy.setLegalBasisDescription(policyRequest.getLegalBasisDescription());
        if (id != null) {
            policy.setPolicyId(id);
        }
        return policy;
    }

    public PolicyResponse mapPolicyToResponse(Policy policy) {
        PolicyResponse response = new PolicyResponse();
        response.setPolicyId(policy.getPolicyId());
        response.setLegalBasisDescription(policy.getLegalBasisDescription());
        InformationType informationType = informationTypeConsumer.getInformationTypeById(policy.getInformationTypeId());
        if (informationType == null) {
            logger.error(String.format("Cannot find InformationType with id: %s", policy.getInformationTypeId()));
            throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find InformationType with id: %s", policy.getInformationTypeId()));
        }
        response.setInformationType(informationType);
        response.setPurpose(Map.of("code", policy.getPurposeCode(), "description", codelistConsumer.getCodelistDescription(ListName.PURPOSE, policy.getPurposeCode())));
        return response;
    }
}
