package no.nav.data.catalog.policies.test.component;

import no.nav.data.catalog.policies.app.AppStarter;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({AppStarter.class})
@ComponentScan(value = "no.nav.data.catalog.policies")
public class ComponentTestConfig {
}

