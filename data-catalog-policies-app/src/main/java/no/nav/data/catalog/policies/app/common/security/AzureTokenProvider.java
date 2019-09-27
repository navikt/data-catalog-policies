package no.nav.data.catalog.policies.app.common.security;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.microsoft.azure.spring.autoconfigure.aad.ServiceEndpoints;
import com.microsoft.azure.spring.autoconfigure.aad.ServiceEndpointsProperties;
import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class AzureTokenProvider {

    private final AADAuthenticationProperties aadAuthProps;
    private final ServiceEndpoints serviceEndpoints;
    private final PoliciesUserProperties policiesUserProperties;
    private final String appIdUrl;
    private final boolean enable;

    private final ExecutorService service = Executors.newFixedThreadPool(1);
    private String token = "";
    private Instant expires = Instant.MIN;

    public AzureTokenProvider(AADAuthenticationProperties aadAuthProps, ServiceEndpointsProperties serviceEndpointsProps,
            PoliciesUserProperties policiesUserProperties, @Value("${azure.app.id.uri}") String appIdUrl, @Value("${security.enabled:true}") boolean enable) {
        this.aadAuthProps = aadAuthProps;
        this.serviceEndpoints = serviceEndpointsProps.getServiceEndpoints(aadAuthProps.getEnvironment());
        this.policiesUserProperties = policiesUserProperties;
        this.appIdUrl = appIdUrl;
        this.enable = enable;
    }

    public String getToken() {
        if (enable && expires.isBefore(Instant.now())) {
            refresh();
        }
        return token;
    }

    private void refresh() {
        try {
            String uri = serviceEndpoints.getAadSigninUri() + aadAuthProps.getTenantId() + "/";
            log.debug("Refreshing azure token authority={}", uri);
            AuthenticationContext context = new AuthenticationContext(uri, true, service);
            AuthenticationResult authenticationResult = context
                    .acquireToken(appIdUrl, aadAuthProps.getClientId(), policiesUserProperties.getUser(), policiesUserProperties.getPwd(), null).get();
            expires = authenticationResult.getExpiresOnDate().toInstant().minusSeconds(60);
            token = authenticationResult.getAccessToken();
            log.info("Acquired new azure token, expires {}", expires);
        } catch (Exception e) {
            log.error("error refreshing azure token", e);
            throw new DataCatalogPoliciesTechnicalException("error refreshing azure token", e);
        }
    }

}