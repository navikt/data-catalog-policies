package no.nav.data.catalog.policies.test.integration;

import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

public class PolicyTestContainer extends PostgreSQLContainer<PolicyTestContainer> {

    private static final String IMAGE_VERSION = "postgres:10.4";
    private static PolicyTestContainer container;

    private PolicyTestContainer() {
        super(IMAGE_VERSION);
    }

    public static PolicyTestContainer getInstance() {
        if (container == null) {
            container = new PolicyTestContainer();
        }
        return container;
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + getInstance().getJdbcUrl(),
                    "spring.datasource.username=" + getInstance().getUsername(),
                    "spring.datasource.password=" + getInstance().getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }
}
