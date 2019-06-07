package no.nav.data.catalog.policies.app.policy.repository;

import no.nav.data.catalog.policies.app.policy.entities.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Page<Policy> findByInformationTypeId(Pageable  pageable, Long informationTypeId);

    boolean existsByInformationTypeIdAndPurposeCode(Long informtionTypeId, String purposeCode);
}