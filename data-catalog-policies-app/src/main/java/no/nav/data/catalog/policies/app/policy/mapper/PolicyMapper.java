package no.nav.data.catalog.policies.app.policy.mapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.policy.domain.CodeResponse;
import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.domain.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class PolicyMapper {

    @Autowired
    private CodelistConsumer codelistConsumer;

    public Policy mapRequestToPolicy(PolicyRequest policyRequest, Long id) {
        Policy policy = new Policy();
        policy.setDatasetId(policyRequest.getDatasetId());
        policy.setDatasetTitle(policyRequest.getDatasetTitle());
        policy.setPurposeCode(policyRequest.getPurposeCode());
        policy.setLegalBasisDescription(policyRequest.getLegalBasisDescription());
        if (id != null) {
            policy.setPolicyId(id);
        }
        if (policyRequest.getExistingPolicy() != null) {
            policy.setCreatedBy(policyRequest.getExistingPolicy().getCreatedBy());
            policy.setCreatedDate(policyRequest.getExistingPolicy().getCreatedDate());
        }
        return policy;
    }

    public PolicyResponse mapPolicyToResponse(Policy policy) {
        PolicyResponse response = new PolicyResponse();
        response.setPolicyId(policy.getPolicyId());
        response.setLegalBasisDescription(policy.getLegalBasisDescription());
        response.setDataset(new Dataset(policy.getDatasetId(), policy.getDatasetTitle()));
        response.setPurpose(new CodeResponse(policy.getPurposeCode(), codelistConsumer.getCodelistDescription(ListName.PURPOSE, policy.getPurposeCode())));
        return response;
    }
}
