package no.nav.data.catalog.policies.test.component;

import no.nav.data.catalog.policies.app.AppStarter;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.testcontainers.containers.PostgreSQLContainer;

import java.util.ArrayList;
import java.util.List;

@Configuration
@Import({AppStarter.class})
@ComponentScan(value = "no.nav.data.catalog.policies.test.component")
public class ComponentTestConfig {
}

