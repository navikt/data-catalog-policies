package no.nav.data.catalog.policies.app.behandlingsgrunnlag;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BehandlingsgrunnlagDistributionRepository extends CrudRepository<BehandlingsgrunnlagDistribution, UUID> {

}