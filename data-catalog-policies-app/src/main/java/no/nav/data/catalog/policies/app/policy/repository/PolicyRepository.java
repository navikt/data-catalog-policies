package no.nav.data.catalog.policies.app.policy.repository;

import no.nav.data.catalog.policies.app.policy.entities.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Page<Policy> findByDatasetId(Pageable pageable, UUID datasetId);

    long countByDatasetId(UUID datasetId);

    boolean existsByDatasetIdAndPurposeCode(UUID datasetId, String purposeCode);
}