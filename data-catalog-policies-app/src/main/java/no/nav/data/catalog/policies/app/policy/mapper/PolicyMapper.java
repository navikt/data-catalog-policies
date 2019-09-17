package no.nav.data.catalog.policies.app.policy.mapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.consumer.DatasetConsumer;
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

    @Autowired
    private DatasetConsumer datasetConsumer;

    public Policy mapRequestToPolicy(PolicyRequest policyRequest, Long id) {
        Policy policy = new Policy();
        policy.setDatasetId(policyRequest.getDatasetId());
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
        try {
            Dataset dataset = datasetConsumer.getDatasetById(policy.getDatasetId());
            response.setDataset(dataset);
        } catch (Exception e) {
            log.trace(String.format("Cannot find Dataset with id: %s", policy.getDatasetId()));
        }
        response.setPurpose(new CodeResponse(policy.getPurposeCode(), codelistConsumer.getCodelistDescription(ListName.PURPOSE, policy.getPurposeCode())));
        return response;
    }
}
