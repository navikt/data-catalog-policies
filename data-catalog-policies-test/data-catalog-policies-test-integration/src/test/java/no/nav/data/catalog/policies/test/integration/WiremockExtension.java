package no.nav.data.catalog.policies.test.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.core.WireMockConfiguration;
import no.nav.data.catalog.policies.app.common.nais.LeaderElectionService;
import no.nav.data.catalog.policies.app.common.util.JsonUtils;
import no.nav.data.catalog.policies.test.integration.util.WiremockResponseTransformer;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.okJson;

public class WiremockExtension implements Extension, BeforeAllCallback, BeforeEachCallback, AfterEachCallback {

    private static final WireMockServer WIREMOCK = new WireMockServer(
            WireMockConfiguration.wireMockConfig()
                    .dynamicPort()
                    .extensions(new WiremockResponseTransformer())
    );

    @Override
    public void beforeAll(ExtensionContext context) {
        getWiremock().start();
        WireMock.configureFor("localhost", getWiremock().port());
    }

    @Override
    public void beforeEach(ExtensionContext context) {
        stubCommon();
    }

    @Override
    public void afterEach(ExtensionContext context) {
        getWiremock().resetMappings();
    }

    private void stubCommon() {
        getWiremock().stubFor(get("/elector").willReturn(okJson(JsonUtils.toJson(LeaderElectionService.getHostInfo()))));
    }

    static WireMockServer getWiremock() {
        return WIREMOCK;
    }
}
