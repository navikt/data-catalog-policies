package no.nav.data.catalog.policies.app.service;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.model.common.PolicyRequest;
import no.nav.data.catalog.policies.app.model.entities.LegalBasis;
import no.nav.data.catalog.policies.app.model.entities.Policy;
import no.nav.data.catalog.policies.app.model.entities.Purpose;
import no.nav.data.catalog.policies.app.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.repository.PolicyRepository;
import no.nav.data.catalog.policies.app.repository.PurposeRepository;
import no.nav.data.catalog.policies.app.service.mapper.PolicyMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

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


    public Page<Policy> getPolicies(Pageable pageable) {
        return policyRepository.findAll(pageable);
    }

    public Policy getPolicy(Long id) {
        Optional<Policy> optionalPolicy = policyRepository.findById(id);
        if (!optionalPolicy.isPresent()) {
            throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find Policy with id: %s", id));
        }
        return optionalPolicy.get();
    }

    public List<Purpose> getPurposes() {
        return purposeRepository.findAll();
    }

    public List<LegalBasis> getLegalBases() {
        return legalBasisRepository.findAll();
    }

    public Policy createPolicy(PolicyRequest policyRequest) {
        Policy policy = mapper.mapRequestToPolicy(policyRequest, null);
        return policyRepository.save(policy);
    }

    public void deletePolicy(Long id) {
        policyRepository.deleteById(id);
    }

    public Policy updatePolicy(Long id, PolicyRequest policyRequest) {
        Policy policy = mapper.mapRequestToPolicy(policyRequest, id);
        return policyRepository.save(policy);
    }
}