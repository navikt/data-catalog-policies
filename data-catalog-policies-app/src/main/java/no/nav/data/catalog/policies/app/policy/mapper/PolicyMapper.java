package no.nav.data.catalog.policies.app.policy.mapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.policy.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import no.nav.data.catalog.policies.app.policy.entities.LegalBasis;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.entities.Purpose;
import no.nav.data.catalog.policies.app.policy.repository.InformationTypeRepository;
import no.nav.data.catalog.policies.app.policy.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.policy.repository.PurposeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@Slf4j
public class PolicyMapper {
    private static final Logger logger = LoggerFactory.getLogger(PolicyMapper.class);

    @Autowired
    private LegalBasisRepository legalBasisRepository;
    @Autowired
    private PurposeRepository purposeRepository;
    @Autowired
    private InformationTypeRepository informationTypeRepository;

    public Policy mapRequestToPolicy(PolicyRequest policyRequest, Long id) {
        Optional<LegalBasis> optionalLegalBasis = legalBasisRepository.findById(policyRequest.getLegalBasisId());
        if (!optionalLegalBasis.isPresent()) {
            logger.error(String.format("Cannot find Legal basis with id: %s", policyRequest.getLegalBasisId()));
            throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find Legal basis with id: %s", policyRequest.getLegalBasisId()));
        }

        LegalBasis legalBasis = optionalLegalBasis.get();
        Optional<Purpose> optionalPurpose = purposeRepository.findById(policyRequest.getPurposeId());
        if (!optionalPurpose.isPresent()) {
            logger.error(String.format("Cannot find Purpose with id: %s", policyRequest.getPurposeId()));
            throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find Purpose with id: %s", policyRequest.getPurposeId()));
        }
        Purpose purpose = optionalPurpose.get();

        Optional<InformationType> optionalInformationType = informationTypeRepository.findById(policyRequest.getInformationTypeId());
        if (!optionalInformationType.isPresent()) {
            logger.error(String.format("Cannot find InformationType with id: %s", policyRequest.getInformationTypeId()));
            throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find InformationType with id: %s", policyRequest.getInformationTypeId()));
        }
        InformationType informationType = optionalInformationType.get();

        Policy policy = new Policy();
        policy.setInformationType(informationType);
        policy.setLegalBasis(legalBasis);
        policy.setPurpose(purpose);
        policy.setLegalBasisDescription(policyRequest.getLegalBasisDescription());
        if (id != null) {
            policy.setPolicyId(id);
        }
        return policy;
    }
}
