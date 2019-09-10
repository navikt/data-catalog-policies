package no.nav.data.catalog.policies.test.integration;

import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import io.confluent.kafka.serializers.KafkaAvroDeserializer;
import io.prometheus.client.CollectorRegistry;
import no.nav.data.catalog.Behandlingsgrunnlag;
import no.nav.data.catalog.policies.app.AppStarter;
import no.nav.data.catalog.policies.app.kafka.KafkaTopicProperties;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.test.integration.IntegrationTestBase.Initializer;
import no.nav.data.catalog.policies.test.integration.kafka.SchemaRegistryContainer;
import no.nav.data.catalog.policies.test.integration.util.WiremockResponseTransformer;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.Deserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.runner.RunWith;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.testcontainers.containers.KafkaContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = {AppStarter.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {Initializer.class, PolicyTestContainer.Initializer.class})
public abstract class IntegrationTestBase {

    protected static final String DATASET_ID_1 = "0702e097-0800-47e1-9fc9-da9fa935c76d";

    private static final String CONFLUENT_VERSION = "5.3.0";

    @ClassRule
    public static PolicyTestContainer postgreSQLContainer = PolicyTestContainer.getInstance();
    @ClassRule
    public static KafkaContainer kafkaContainer = new KafkaContainer(CONFLUENT_VERSION);
    @ClassRule
    public static SchemaRegistryContainer schemaRegistryContainer = new SchemaRegistryContainer(CONFLUENT_VERSION, kafkaContainer);
    @ClassRule
    public static WireMockClassRule wiremock = new WireMockClassRule(
            WireMockConfiguration.wireMockConfig()
                    .dynamicPort()
                    .extensions(new WiremockResponseTransformer())
    );

    @Autowired
    protected PolicyRepository policyRepository;
    @Autowired
    protected KafkaTopicProperties topicProperties;

    @Before
    public void setUpAbstract() {
        IntegrationTestConfig.mockDataCatalogBackend(wiremock);
        policyRepository.deleteAll();
    }

    @After
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
                    "wiremock.server.port=" + wiremock.port(),
                    "KAFKA_BOOTSTRAP_SERVERS=" + kafkaContainer.getBootstrapServers(),
                    "KAFKA_SCHEMA_REGISTRY_URL=" + schemaRegistryContainer.getAddress()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
