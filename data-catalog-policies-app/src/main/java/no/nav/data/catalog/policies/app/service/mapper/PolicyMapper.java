package no.nav.data.catalog.policies.app.service.mapper;

import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesFunctionalException;
import no.nav.data.catalog.policies.app.model.common.PolicyRequest;
import no.nav.data.catalog.policies.app.model.entities.LegalBasis;
import no.nav.data.catalog.policies.app.model.entities.Policy;
import no.nav.data.catalog.policies.app.model.entities.Purpose;
import no.nav.data.catalog.policies.app.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.repository.PurposeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PolicyMapper {
    @Autowired
    private LegalBasisRepository legalBasisRepository;
    @Autowired
    private PurposeRepository purposeRepository;

    public Policy mapRequestToPolicy(PolicyRequest policyRequest) {
        Policy policy = new Policy();
        Optional<LegalBasis> optionalLegalBasis = legalBasisRepository.findById(policyRequest.getLegalBasisId());
        if (!optionalLegalBasis.isPresent()) {
            throw new DataCatalogPoliciesFunctionalException(String.format("Cannot find Legal basis with id: %s", policyRequest.getLegalBasisId()));
        }
        LegalBasis legalBasis = optionalLegalBasis.get();
        Optional<Purpose> optionalPurpose = purposeRepository.findById(policyRequest.getPurposeId());
        if (!optionalPurpose.isPresent()) {
            throw new DataCatalogPoliciesFunctionalException(String.format("Cannot find Purpose with id: %s", policyRequest.getPurposeId()));
        }
        Purpose purpose = optionalPurpose.get();
        policy.setInformationTypeId(policyRequest.getInformationTypeId());
        policy.setLegalBasis(legalBasis);
        policy.setPurpose(purpose);
        return policy;
    }
}
