package no.nav.data.catalog.policies.app.policy.repository;

import no.nav.data.catalog.policies.app.policy.entities.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Page<Policy> findByDatasetId(Pageable pageable, String datasetId);

    long countByDatasetId(String datasetId);

    boolean existsByDatasetIdAndPurposeCode(String datasetId, String purposeCode);

    @Query("select datasetTitle from Policy where purposeCode = ?1")
    List<String> selectDatasetTitleByPurposeCode(String purposeCode);
}