package no.nav.data.catalog.policies.app.repository;

import no.nav.data.catalog.policies.app.model.entities.Purpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PurposeRepository extends JpaRepository<Purpose, String> {
}
