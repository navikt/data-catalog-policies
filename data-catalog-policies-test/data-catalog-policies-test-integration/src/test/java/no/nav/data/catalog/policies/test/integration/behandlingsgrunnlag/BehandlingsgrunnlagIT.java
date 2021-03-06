package no.nav.data.catalog.policies.test.integration.behandlingsgrunnlag;

import no.nav.data.catalog.Behandlingsgrunnlag;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagDistributionRepository;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagService;
import no.nav.data.catalog.policies.test.integration.KafkaIntegrationTestBase;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Duration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.junit.jupiter.api.Assertions.assertEquals;

class BehandlingsgrunnlagIT extends KafkaIntegrationTestBase {

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
        createPolicy(3, (index, policy) -> {
            // Inactive policy should not be sent
            if (index == 1) {
                policy.setPurposeCode("other-purpose");
                policy.setDatasetTitle("other-title");
                policy.setTom(LocalDate.now().minusDays(1));
            } else if (index == 2) {
                policy.setDatasetTitle(DATASET_TITLE + "2");
            }
        });

        behandlingsgrunnlagService.scheduleDistributeForPurpose(PURPOSE_CODE1);
        behandlingsgrunnlagService.distributeAll();

        await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> assertEquals(0L, repository.count()));

        ConsumerRecord<String, Behandlingsgrunnlag> singleRecord = KafkaTestUtils.getSingleRecord(consumer, topicProperties.getBehandlingsgrunnlag());

        assertEquals(PURPOSE_CODE1, singleRecord.key());
        assertEquals(PURPOSE_CODE1, singleRecord.value().getPurpose());
        assertThat(singleRecord.value().getDatasets()).contains(DATASET_TITLE, DATASET_TITLE + "2");
    }
}
