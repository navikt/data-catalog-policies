package no.nav.data.catalog.policies.app.policy.repository;

import no.nav.data.catalog.policies.app.policy.entities.Policy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PolicyRepository extends JpaRepository<Policy, Long> {
    Page<Policy> findByInformationTypeInformationTypeId(Pageable  pageable, Long informationTypeId);

//    @Query("select case when count(p)> 0 then true else false end from Policy p where InformationType.informationTypeId = :informationTypeId and upper(purposeCode) = upper(:purposeCode)")
//    boolean existsByInformationTypeInformationTypeIdAndPurposeCode(@Param("model") String model);
    boolean existsByInformationTypeInformationTypeIdAndPurposeCode(Long informtionTypeId, String purposeCode);
}