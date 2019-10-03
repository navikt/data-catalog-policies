package no.nav.data.catalog.policies.test.integration;

import io.prometheus.client.CollectorRegistry;
import no.nav.data.catalog.policies.app.AppStarter;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagDistributionRepository;
import no.nav.data.catalog.policies.app.common.security.AzureTokenProvider;
import no.nav.data.catalog.policies.app.kafka.KafkaTopicProperties;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.test.integration.IntegrationTestBase.Initializer;
import no.nav.data.catalog.policies.test.integration.kafka.KafkaContainer;
import no.nav.data.catalog.policies.test.integration.kafka.SchemaRegistryContainer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;

import java.time.LocalDate;
import java.util.UUID;
import java.util.function.BiConsumer;

import static org.mockito.Mockito.when;

/**
 * Extension order is important and SpringBootTest contains the spring extension
 */
@ActiveProfiles("test")
@ExtendWith(WiremockExtension.class)
@SpringBootTest(classes = {AppStarter.class}, webEnvironment = WebEnvironment.RANDOM_PORT)
@ContextConfiguration(initializers = {Initializer.class})
public abstract class IntegrationTestBase {

    protected static final String DATASET_ID_1 = "0702e097-0800-47e1-9fc9-da9fa935c76d";
    protected static final String LEGAL_BASIS_DESCRIPTION1 = "Legal basis 1";
    protected static final String PURPOSE_CODE1 = "TEST1";
    protected static final String DATASET_TITLE = "Sivilstand";

    private static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:10.4");

    static {
        // The limited junit5 support for testcontainers do not support containers to live across separate ITests
        postgreSQLContainer.start();
    }

    @Autowired
    protected PolicyRepository policyRepository;
    @Autowired
    protected KafkaTopicProperties topicProperties;
    @Autowired
    protected BehandlingsgrunnlagDistributionRepository behandlingsgrunnlagDistributionRepository;
    @MockBean
    private AzureTokenProvider azureTokenProvider;

    @BeforeEach
    public void setUpAbstract() {
        IntegrationTestConfig.mockDataCatalogBackend();
        when(azureTokenProvider.getToken()).thenReturn("token");
        policyRepository.deleteAll();
    }

    @AfterEach
    public void cleanUpAbstract() {
        policyRepository.deleteAll();
        CollectorRegistry.defaultRegistry.clear();
    }

    protected void createPolicy(int rows) {
        createPolicy(rows, (i, p) -> {
        });
    }

    protected void createPolicy(int rows, BiConsumer<Integer, Policy> callback) {
        int i = 0;
        while (i++ < rows) {
            Policy policy = new Policy();
            policy.setDatasetId(i == 1 ? DATASET_ID_1 : UUID.randomUUID().toString());
            policy.setDatasetTitle(DATASET_TITLE);
            policy.setLegalBasisDescription(LEGAL_BASIS_DESCRIPTION1);
            policy.setPurposeCode(PURPOSE_CODE1);
            policy.setFom(LocalDate.now());
            policy.setTom(LocalDate.now());
            callback.accept(i, policy);
            policyRepository.save(policy);
        }
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "wiremock.server.port=" + WiremockExtension.getWiremock().port(),
                    "KAFKA_BOOTSTRAP_SERVERS=" + KafkaContainer.getAddress(),
                    "KAFKA_SCHEMA_REGISTRY_URL=" + SchemaRegistryContainer.getAddress()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
