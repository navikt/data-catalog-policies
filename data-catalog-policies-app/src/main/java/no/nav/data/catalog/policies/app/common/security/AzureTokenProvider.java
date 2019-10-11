package no.nav.data.catalog.policies.app.common.security;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import com.microsoft.azure.spring.autoconfigure.aad.ServiceEndpoints;
import com.microsoft.azure.spring.autoconfigure.aad.ServiceEndpointsProperties;
import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.Proxy;
import java.time.Instant;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
public class AzureTokenProvider {

    private final AADAuthenticationProperties aadAuthProps;
    private final Proxy proxy;
    private final ServiceEndpoints serviceEndpoints;
    private final String appIdUrl;
    private final boolean enable;

    private final ExecutorService service = Executors.newFixedThreadPool(1);
    private String token = "";
    private Instant expires = Instant.MIN;

    public AzureTokenProvider(AADAuthenticationProperties aadAuthProps, ServiceEndpointsProperties serviceEndpointsProps,
            @Value("${azure.app.id.uri}") String appIdUrl, @Value("${security.enabled:true}") boolean enable, Proxy proxy) {
        this.aadAuthProps = aadAuthProps;
        this.proxy = proxy;
        this.serviceEndpoints = serviceEndpointsProps.getServiceEndpoints(aadAuthProps.getEnvironment());
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
            AuthenticationContext context = createContext();
            AuthenticationResult authenticationResult = context
                    .acquireToken(appIdUrl, new ClientCredential(aadAuthProps.getClientId(), aadAuthProps.getClientSecret()), null)
                    .get();
            expires = authenticationResult.getExpiresOnDate().toInstant().minusSeconds(60);
            token = authenticationResult.getAccessToken();
            log.info("Acquired new azure token, expires {}", expires);
        } catch (Exception e) {
            log.error("error refreshing azure token", e);
            throw new DataCatalogPoliciesTechnicalException("error refreshing azure token", e);
        }
    }

    private AuthenticationContext createContext() throws MalformedURLException {
        String uri = serviceEndpoints.getAadSigninUri() + aadAuthProps.getTenantId() + "/";
        log.debug("Refreshing azure token authority={}", uri);
        AuthenticationContext context = new AuthenticationContext(uri, true, service);
        context.setProxy(proxy);
        return context;
    }

}
