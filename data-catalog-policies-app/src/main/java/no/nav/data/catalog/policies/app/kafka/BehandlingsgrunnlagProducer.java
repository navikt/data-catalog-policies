package no.nav.data.catalog.policies.app.kafka;

import io.prometheus.client.Collector;
import io.prometheus.client.Counter;
import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.Behandlingsgrunnlag;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class BehandlingsgrunnlagProducer {

    private Counter counter;
    private final String topic;
    private final KafkaTemplate<String, Behandlingsgrunnlag> kafkaTemplate;

    @SuppressWarnings("unchecked")
    public BehandlingsgrunnlagProducer(
            KafkaTopicProperties topics,
            KafkaTemplate kafkaTemplate
    ) {
        this.topic = topics.getBehandlingsgrunnlag();
        this.kafkaTemplate = (KafkaTemplate<String, Behandlingsgrunnlag>) kafkaTemplate;

        initMetrics();
    }

    public boolean sendBehandlingsgrunnlag(String purpose, List<String> datasets) {
        var behandlingsgrunnlag = new Behandlingsgrunnlag(purpose, new ArrayList<>(datasets));
        log.info("Sender behandlingsgrunnlag {} på topic {}", behandlingsgrunnlag, topic);

        try {
            kafkaTemplate.send(topic, purpose, behandlingsgrunnlag).get();
            counter.labels("ok").inc();
            return true;
        } catch (Exception e) {
            log.error("Failed to send message to topic " + topic, e);
            counter.labels("feil").inc();
            return false;
        }
    }

    private void initMetrics() {
        counter = Counter.build()
                .name(Collector.sanitizeMetricName(String.format("kafka_producer_%s_counter", topic)))
                .help(String.format("Kafka melding lagt på topic %s", topic))
                .labelNames("resultat")
                .register();
    }

}
