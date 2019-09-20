package no.nav.data.catalog.policies.test.integration.behandlingsgrunnlag;

import no.nav.data.catalog.Behandlingsgrunnlag;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagDistributionRepository;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagService;
import no.nav.data.catalog.policies.test.integration.IntegrationTestBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
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
    private Consumer<String, Behandlingsgrunnlag> consumer;

    @BeforeEach
    void setUp() {
        repository.deleteAll();
        consumer = behandlingsgrunnlagConsumer();
        // Clean out topic
        KafkaTestUtils.getRecords(consumer, 0L);
    }

    @Test
    void produserBehandlingsgrunnlag() {
        createPolicy("desc", "PURPOSE", 1);

        behandlingsgrunnlagService.scheduleDistributeForPurpose("PURPOSE");
        behandlingsgrunnlagService.distributeAll();

        await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> assertEquals(0L, repository.count()));

        ConsumerRecord<String, Behandlingsgrunnlag> singleRecord = KafkaTestUtils.getSingleRecord(consumer, topicProperties.getBehandlingsgrunnlag());

        assertEquals("PURPOSE", singleRecord.key());
        assertEquals("PURPOSE", singleRecord.value().getPurpose());
        assertEquals(List.of("title"), singleRecord.value().getDatasets());
    }
}
