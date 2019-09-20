package no.nav.data.catalog.policies.test.integration;

import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.prometheus.client.CollectorRegistry;
import no.nav.data.catalog.Behandlingsgrunnlag;
import no.nav.data.catalog.policies.app.AppStarter;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagDistributionRepository;
import no.nav.data.catalog.policies.app.kafka.KafkaTopicProperties;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.test.integration.IntegrationTestBase.Initializer;
import no.nav.data.catalog.policies.test.integration.kafka.SchemaRegistryContainer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

/**
 * Extension order is important and SpringBootTest contains the spring extension
 */
@ActiveProfiles("test")
@ExtendWith(WiremockExtension.class)
@SpringBootTest(classes = {AppStarter.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {Initializer.class})
public abstract class IntegrationTestBase {

    protected static final String DATASET_ID_1 = "0702e097-0800-47e1-9fc9-da9fa935c76d";

    private static final String CONFLUENT_VERSION = "5.3.0";

    private static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:10.4");
    private static KafkaContainer kafkaContainer = new KafkaContainer(CONFLUENT_VERSION);
    private static SchemaRegistryContainer schemaRegistryContainer = new SchemaRegistryContainer(CONFLUENT_VERSION, kafkaContainer);

    static {
        // The limited junit5 support for testcontainers do not support containers to live across separate ITests
        Stream.of(postgreSQLContainer, kafkaContainer).parallel().forEach(GenericContainer::start);
        schemaRegistryContainer.start();
    }

    @Autowired
    protected PolicyRepository policyRepository;
    @Autowired
    protected KafkaTopicProperties topicProperties;
    @Autowired
    protected BehandlingsgrunnlagDistributionRepository behandlingsgrunnlagDistributionRepository;

    @BeforeEach
    public void setUpAbstract() {
        IntegrationTestConfig.mockDataCatalogBackend();
        policyRepository.deleteAll();
    }

    @AfterEach
    public void cleanUpAbstract() {
        policyRepository.deleteAll();
        CollectorRegistry.defaultRegistry.clear();
    }

    protected void createPolicy(String legalBasisDescription, String purposeCode, int rows) {
        int i = 0;
        while (i++ < rows) {
            Policy policy = new Policy();
            policy.setDatasetId(i == 1 ? DATASET_ID_1 : UUID.randomUUID().toString());
            policy.setDatasetTitle("title");
            policy.setLegalBasisDescription(legalBasisDescription);
            policy.setPurposeCode(purposeCode);
            policyRepository.save(policy);
        }
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

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "wiremock.server.port=" + WiremockExtension.getWiremock().port(),
                    "KAFKA_BOOTSTRAP_SERVERS=" + kafkaContainer.getBootstrapServers(),
                    "KAFKA_SCHEMA_REGISTRY_URL=" + schemaRegistryContainer.getAddress()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
