package no.nav.data.catalog.policies.app.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.prometheus.client.CollectorRegistry;
import io.prometheus.client.hotspot.DefaultExports;
import no.nav.data.catalog.policies.app.common.auditing.AuditorAwareImpl;
import no.nav.data.catalog.policies.app.common.util.JsonUtils;
import no.nav.data.catalog.policies.app.common.web.TraceHeaderRequestInterceptor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorAware")
public class CommonConfig {

    @Bean
    public AuditorAware<String> auditorAware() {
        return new AuditorAwareImpl();
    }

    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder
                .additionalInterceptors(TraceHeaderRequestInterceptor.fullInterceptor())
                .build();
    }

    @Primary
    @Bean
    public ObjectMapper objectMapper() {
        return JsonUtils.getObjectMapper();
    }

    @Bean
    public MappingJackson2HttpMessageConverter mappingJackson2HttpMessageConverter() {
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        jsonConverter.setObjectMapper(objectMapper());
        return jsonConverter;
    }

    @Bean
    public Proxy proxy(NavProperties navProperties) {
        return new Proxy(Type.HTTP, new InetSocketAddress(navProperties.getProxyHost(), navProperties.getProxyPort()));
    }


    /**
     * Make sure spring uses the defaultRegistry
     */
    @Bean
    public CollectorRegistry collectorRegistry() {
        DefaultExports.initialize();
        return CollectorRegistry.defaultRegistry;
    }
}
