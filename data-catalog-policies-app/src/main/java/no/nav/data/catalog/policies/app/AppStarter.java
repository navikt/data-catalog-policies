package no.nav.data.catalog.policies.app;

import com.microsoft.azure.spring.autoconfigure.aad.AADOAuth2AutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication(exclude = {AADOAuth2AutoConfiguration.class})
@EnableCaching
public class AppStarter {

    public static void main(String[] args) {
        SpringApplication.run(AppStarter.class, args);
    }
}
