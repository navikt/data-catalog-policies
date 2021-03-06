package no.nav.data.catalog.policies.test.integration;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import no.nav.data.catalog.Behandlingsgrunnlag;
import no.nav.data.catalog.policies.test.integration.kafka.KafkaContainer;
import no.nav.data.catalog.policies.test.integration.kafka.SchemaRegistryContainer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class KafkaIntegrationTestBase extends IntegrationTestBase {

    private static final String CONFLUENT_VERSION = "5.3.0";

    private static KafkaContainer kafkaContainer = new KafkaContainer(CONFLUENT_VERSION);
    private static SchemaRegistryContainer schemaRegistryContainer = new SchemaRegistryContainer(CONFLUENT_VERSION, kafkaContainer);

    static {
        // The limited junit5 support for testcontainers do not support containers to live across separate ITests
        kafkaContainer.start();
        schemaRegistryContainer.start();
    }

    protected Consumer<String, Behandlingsgrunnlag> behandlingsgrunnlagConsumer() {
        Map<String, Object> configs = new HashMap<>(KafkaTestUtils.consumerProps(kafkaContainer.getBootstrapServers(), "policy-itest", "false"));
        configs.put("specific.avro.reader", "true");
        configs.put("schema.registry.url", schemaRegistryContainer.getAddress());
        configs.put(ConsumerConfig.CLIENT_ID_CONFIG, "policy");
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaAvroDeserializer.class.getName());
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        var consumer = new DefaultKafkaConsumerFactory<>(configs, (Deserializer<String>) null, (Deserializer<Behandlingsgrunnlag>) null).createConsumer();
        consumer.subscribe(Collections.singleton(topicProperties.getBehandlingsgrunnlag()));
        return consumer;
    }

}
