package no.nav.data.catalog.policies.app.behandlingsgrunnlag;

import no.nav.data.catalog.policies.app.kafka.BehandlingsgrunnlagProducer;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.transaction.Transactional;

@Service
public class BehandlingsgrunnlagService {

    private final BehandlingsgrunnlagDistributionRepository distributionRepository;
    private final PolicyRepository policyRepository;
    private final BehandlingsgrunnlagProducer behandlingsgrunnlagProducer;

    private LocalDateTime lastDistribution = LocalDateTime.MIN;

    public BehandlingsgrunnlagService(BehandlingsgrunnlagDistributionRepository distributionRepository,
            PolicyRepository policyRepository, BehandlingsgrunnlagProducer behandlingsgrunnlagProducer) {
        this.distributionRepository = distributionRepository;
        this.policyRepository = policyRepository;
        this.behandlingsgrunnlagProducer = behandlingsgrunnlagProducer;
        scheduleDistributions();
    }

    @Transactional
    public void scheduleDistributeForPurpose(String purpose) {
        var distribution = distributionRepository.findById(purpose)
                .orElse(BehandlingsgrunnlagDistribution.newForPurpose(purpose))
                .markChanged();
        distributionRepository.save(distribution);
        distributeAll();
    }

    private void distributeAll() {
        if (lastDistribution.isAfter(LocalDateTime.now().minusSeconds(30))) {
            return;
        }
        distributionRepository.findAllByStatus(DistributionStatus.CHANGED).stream()
                .map(BehandlingsgrunnlagDistribution::getPurpose)
                .forEach(this::distribute);
        lastDistribution = LocalDateTime.now();
    }

    private void distribute(String purpose) {
        List<String> datasets = policyRepository.selectDatasetTitleByPurposeCode(purpose);
        if (behandlingsgrunnlagProducer.sendBehandlingsgrunnlag(purpose, datasets)) {
            distributionRepository.save(distributionRepository.findById(purpose).orElseThrow().markDistributed());
        }
    }

    private void scheduleDistributions() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setThreadNamePrefix("BehGrnlgDist");
        scheduler.initialize();
        scheduler.scheduleAtFixedRate(this::distributeAll, Instant.now().plus(5, ChronoUnit.MINUTES), Duration.ofMinutes(5));
    }
}
