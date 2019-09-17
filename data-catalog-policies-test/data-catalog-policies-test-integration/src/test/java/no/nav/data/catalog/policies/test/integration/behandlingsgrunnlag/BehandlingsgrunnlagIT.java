package no.nav.data.catalog.policies.test.integration.behandlingsgrunnlag;

import no.nav.data.catalog.Behandlingsgrunnlag;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagDistributionRepository;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagService;
import no.nav.data.catalog.policies.test.integration.IntegrationTestBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.awaitility.Duration;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class BehandlingsgrunnlagIT extends IntegrationTestBase {

    @Autowired
    private BehandlingsgrunnlagService behandlingsgrunnlagService;
    @Autowired
    private BehandlingsgrunnlagDistributionRepository repository;

    @Test
    public void produserBehandlingsgrunnlag() {
        String purpose = "PURPOSE";
        createPolicy("desc", purpose, 1);

        behandlingsgrunnlagService.scheduleDistributeForPurpose(purpose);
        behandlingsgrunnlagService.distributeAll();

        await().atMost(Duration.TEN_SECONDS).untilAsserted(() -> assertThat(repository.count(), is(0L)));

        ConsumerRecord<String, Behandlingsgrunnlag> singleRecord = KafkaTestUtils.getSingleRecord(behandlingsgrunnlagConsumer(), topicProperties.getBehandlingsgrunnlag());

        assertThat(singleRecord.key(), is(purpose));
        assertThat(singleRecord.value().getPurpose().toString(), is(purpose));
        assertThat(singleRecord.value().getDatasets(), hasSize(1));
        assertThat(singleRecord.value().getDatasets().get(0).toString(), is("title"));
    }
}
