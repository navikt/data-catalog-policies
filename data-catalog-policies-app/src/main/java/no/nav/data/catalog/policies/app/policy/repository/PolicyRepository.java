package no.nav.data.catalog.policies.app.policy.repository;

import no.nav.data.catalog.policies.app.policy.entities.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.persistence.OrderBy;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Page<Policy> findByInformationTypeInformationTypeId(Pageable  pageable, Long informationTypeId);
}