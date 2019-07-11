package no.nav.data.catalog.policies.test.component;

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
//        System.setProperty("POSTGRES_URL", container.getJdbcUrl());
//        System.setProperty("POSTGRES_USER", container.getUsername());
//        System.setProperty("POSTGRES_PASSWORD", container.getPassword());
    }

    @Override
    public void stop() {
        //do nothing, JVM handles shut down
    }
}
