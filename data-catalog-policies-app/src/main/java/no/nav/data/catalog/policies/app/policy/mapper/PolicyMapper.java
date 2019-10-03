package no.nav.data.catalog.policies.app.policy.mapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.consumer.CodelistConsumer;
import no.nav.data.catalog.policies.app.policy.domain.CodeResponse;
import no.nav.data.catalog.policies.app.policy.domain.DatasetResponse;
import no.nav.data.catalog.policies.app.policy.domain.ListName;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.domain.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

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
        policy.setFom(parse(policyRequest.getFom(), LocalDate.of(1, 1, 1)));
        policy.setTom(parse(policyRequest.getTom(), LocalDate.of(9999, 12, 31)));
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
        response.setDataset(new DatasetResponse(policy.getDatasetId(), policy.getDatasetTitle()));
        response.setPurpose(new CodeResponse(policy.getPurposeCode(), codelistConsumer.getCodelistDescription(ListName.PURPOSE, policy.getPurposeCode())));
        response.setFom(policy.getFom());
        response.setTom(policy.getTom());
        response.setActive(policy.isActive());
        return response;
    }

    private LocalDate parse(String date, LocalDate defaultValue) {
        return date == null ? defaultValue : LocalDate.parse(date);
    }
}
