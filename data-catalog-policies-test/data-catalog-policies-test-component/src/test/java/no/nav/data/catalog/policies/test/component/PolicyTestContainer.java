package no.nav.data.catalog.policies.test.component;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.PostgreSQLContainer;

@Slf4j
public class PolicyTestContainer extends PostgreSQLContainer<PolicyTestContainer> {

    private static final Logger logger = LoggerFactory.getLogger(PolicyTestContainer.class);
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
    public void start() {
        super.start();
        logger.info("Starter postgreskontainer");
        logger.info("POSTGRES_URL: " + container.getJdbcUrl());
        logger.info("POSTGRES_USER: " + container.getUsername());
        logger.info("POSTGRES_PASSWORD: " + container.getPassword());
        System.setProperty("POSTGRES_URL", container.getJdbcUrl());
        System.setProperty("POSTGRES_USER", container.getUsername());
        System.setProperty("POSTGRES_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
