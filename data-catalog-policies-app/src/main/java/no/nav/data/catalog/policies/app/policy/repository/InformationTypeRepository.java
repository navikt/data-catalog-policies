package no.nav.data.catalog.policies.app.policy.repository;

import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InformationTypeRepository extends JpaRepository<InformationType, Long> {
    Optional<InformationType> findByName(@Param("name") String name);
}