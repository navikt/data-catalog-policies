package no.nav.data.catalog.policies.app.service;

import no.nav.data.catalog.policies.app.model.common.PolicyRequest;
import no.nav.data.catalog.policies.app.model.entities.LegalBasis;
import no.nav.data.catalog.policies.app.model.entities.Policy;
import no.nav.data.catalog.policies.app.model.entities.Purpose;
import no.nav.data.catalog.policies.app.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.repository.PolicyRepository;
import no.nav.data.catalog.policies.app.repository.PurposeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PolicyService {
    @Autowired
    private PolicyMapper mapper;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private LegalBasisRepository legalBasisRepository;

    @Autowired
    private PurposeRepository purposeRepository;


    public List<Policy> getPolicies() {
        return policyRepository.findAll();
    }

    public List<Purpose> getPurposes() {
        return purposeRepository.findAll();
    }

    public List<LegalBasis> getLegalBasis() {
        return legalBasisRepository.findAll();
    }

    public Policy createPolicy(PolicyRequest policyRequest) {
        Policy policy = mapper.mapRequestToPolicy(policyRequest);
        return policyRepository.save(policy);
    }
}
