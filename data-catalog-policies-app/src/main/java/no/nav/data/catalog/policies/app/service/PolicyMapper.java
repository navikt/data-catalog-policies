package no.nav.data.catalog.policies.app.service;

import no.nav.data.catalog.policies.app.model.common.PolicyRequest;
import no.nav.data.catalog.policies.app.model.entities.LegalBasis;
import no.nav.data.catalog.policies.app.model.entities.Policy;
import no.nav.data.catalog.policies.app.model.entities.Purpose;
import no.nav.data.catalog.policies.app.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.repository.PurposeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PolicyMapper {
    @Autowired
    private LegalBasisRepository legalBasisRepository;
    @Autowired
    private PurposeRepository purposeRepository;

    public Policy mapRequestToPolicy(PolicyRequest policyRequest) {
        Policy policy = new Policy();
        LegalBasis legalBasis = legalBasisRepository.findById(policyRequest.getLegalBasisId()).get();
        Purpose purpose = purposeRepository.findById(policyRequest.getPurposeId()).get();
        policy.setInformationTypeId(policyRequest.getInformationTypeId());
        policy.setLegalBasis(legalBasis);
        policy.setPurpose(purpose);
        return policy;
    }
}
