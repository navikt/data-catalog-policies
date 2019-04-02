package no.nav.data.catalog.policies.app.repository;

import no.nav.data.catalog.policies.app.model.Policy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
}