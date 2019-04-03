package no.nav.data.catalog.policies.app.repository;

import no.nav.data.catalog.policies.app.model.entities.LegalBasis;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LegalBasisRepository extends JpaRepository<LegalBasis, Long> {
}
