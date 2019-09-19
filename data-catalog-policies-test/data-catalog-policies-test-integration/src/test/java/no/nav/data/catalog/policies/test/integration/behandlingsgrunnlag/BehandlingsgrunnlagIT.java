package no.nav.data.catalog.policies.test.integration.behandlingsgrunnlag;

import no.nav.data.catalog.Behandlingsgrunnlag;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagDistributionRepository;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagService;
import no.nav.data.catalog.policies.test.integration.IntegrationTestBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BehandlingsgrunnlagIT extends IntegrationTestBase {

    @Autowired
    private BehandlingsgrunnlagService behandlingsgrunnlagService;
    @Autowired
    private BehandlingsgrunnlagDistributionRepository repository;

    @Test
    void produserBehandlingsgrunnlag() {
        String purpose = "PURPOSE";
        createPolicy("desc", purpose, 1);

        behandlingsgrunnlagService.scheduleDistributeForPurpose(purpose);
        behandlingsgrunnlagService.distributeAll();

        await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> assertEquals(0L, repository.count()));

        ConsumerRecord<String, Behandlingsgrunnlag> singleRecord = KafkaTestUtils.getSingleRecord(behandlingsgrunnlagConsumer(), topicProperties.getBehandlingsgrunnlag());

        assertEquals(purpose, singleRecord.key());
        assertEquals(purpose, singleRecord.value().getPurpose());
        assertEquals(List.of("title"), singleRecord.value().getDatasets());
    }
}
