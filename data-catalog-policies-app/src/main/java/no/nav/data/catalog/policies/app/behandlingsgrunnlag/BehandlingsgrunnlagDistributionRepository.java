package no.nav.data.catalog.policies.app.behandlingsgrunnlag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BehandlingsgrunnlagDistributionRepository extends JpaRepository<BehandlingsgrunnlagDistribution, UUID> {

}