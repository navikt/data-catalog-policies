package no.nav.data.catalog.policies.app.common.security;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import com.microsoft.azure.spring.autoconfigure.aad.AADAuthenticationProperties;
import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesTechnicalException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class AzureTokenProvider {

    private final AADAuthenticationProperties aadAuthProps;
    private final AuthenticationContext authenticationContext;
    private final boolean enable;

    private String token = "";
    private Instant expires = Instant.MIN;

    public AzureTokenProvider(AADAuthenticationProperties aadAuthProps, AuthenticationContext authenticationContext, @Value("${security.client.enabled:true}") boolean enable) {
        this.aadAuthProps = aadAuthProps;
        this.authenticationContext = authenticationContext;
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
            AuthenticationResult authenticationResult = authenticationContext
                    .acquireToken(aadAuthProps.getAppIdUri(), new ClientCredential(aadAuthProps.getClientId(), aadAuthProps.getClientSecret()), null)
                    .get();
            expires = authenticationResult.getExpiresOnDate().toInstant().minusSeconds(60);
            token = authenticationResult.getAccessToken();
            log.info("Acquired new azure token, expires {}", expires);
        } catch (Exception e) {
            log.error("error refreshing azure token", e);
            throw new DataCatalogPoliciesTechnicalException("error refreshing azure token", e);
        }
    }

}
