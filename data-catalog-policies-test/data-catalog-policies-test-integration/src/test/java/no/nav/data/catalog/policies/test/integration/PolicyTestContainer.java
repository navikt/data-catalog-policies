package no.nav.data.catalog.policies.test.integration;

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
    public void start() {
        super.start();
        System.setProperty("spring.datasource.url", container.getJdbcUrl());
        System.setProperty("spring.datasource.username", container.getUsername());
        System.setProperty("spring.datasource.password", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
