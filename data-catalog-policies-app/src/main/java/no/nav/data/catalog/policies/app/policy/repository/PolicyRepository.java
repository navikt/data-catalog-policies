package no.nav.data.catalog.policies.app.policy.repository;

import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import javax.transaction.Transactional;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {

    Page<Policy> findByDatasetId(Pageable pageable, String datasetId);

    /**
     * For some reason deleteBy methods are not transactional by default
     */
    @Transactional
    long deleteByDatasetId(String datasetId);

    long countByDatasetId(String datasetId);

    boolean existsByDatasetIdAndPurposeCode(String datasetId, String purposeCode);

    @Query("select new no.nav.data.catalog.policies.app.policy.domain.Dataset(datasetId, datasetTitle) from Policy where purposeCode = ?1")
    List<Dataset> findDatasetsByPurposeCode(String purposeCode);

}