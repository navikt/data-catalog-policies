package no.nav.data.catalog.policies.app.behandlingsgrunnlag;

import no.nav.data.catalog.policies.app.common.nais.LeaderElectionService;
import no.nav.data.catalog.policies.app.kafka.BehandlingsgrunnlagProducer;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BehandlingsgrunnlagService {

    private final BehandlingsgrunnlagDistributionRepository distributionRepository;
    private final PolicyRepository policyRepository;
    private final BehandlingsgrunnlagProducer behandlingsgrunnlagProducer;
    private final LeaderElectionService leaderElectionService;

    public BehandlingsgrunnlagService(BehandlingsgrunnlagDistributionRepository distributionRepository,
            PolicyRepository policyRepository, BehandlingsgrunnlagProducer behandlingsgrunnlagProducer,
            LeaderElectionService leaderElectionService) {
        this.distributionRepository = distributionRepository;
        this.policyRepository = policyRepository;
        this.behandlingsgrunnlagProducer = behandlingsgrunnlagProducer;
        this.leaderElectionService = leaderElectionService;
        scheduleDistributions();
    }

    public void scheduleDistributeForPurpose(String purpose) {
        distributionRepository.save(BehandlingsgrunnlagDistribution.newForPurpose(purpose));
    }

    public void distributeAll() {
        if (!leaderElectionService.isLeader()) {
            return;
        }
        distributionRepository.findAll().forEach(this::distribute);
    }

    private void distribute(BehandlingsgrunnlagDistribution distribution) {
        List<String> datasets = policyRepository.selectDatasetTitleByPurposeCode(distribution.getPurpose());
        if (behandlingsgrunnlagProducer.sendBehandlingsgrunnlag(distribution.getPurpose(), datasets)) {
            distributionRepository.deleteById(distribution.getId());
        }
    }

    private void scheduleDistributions() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("BehGrnlgDist");
        scheduler.initialize();
        scheduler.scheduleAtFixedRate(this::distributeAll, Instant.now().plus(1, ChronoUnit.MINUTES), Duration.ofMinutes(1));
    }
}
