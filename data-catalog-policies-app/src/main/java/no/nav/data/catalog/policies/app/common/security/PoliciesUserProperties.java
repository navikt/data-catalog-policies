package no.nav.data.catalog.policies.app.common.security;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("policies.fss")
public class PoliciesUserProperties {

    private String user;
    private String pwd;

}
