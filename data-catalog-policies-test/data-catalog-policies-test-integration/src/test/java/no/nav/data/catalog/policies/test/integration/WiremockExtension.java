package no.nav.data.catalog.policies.test.integration;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.Extension;
import org.junit.jupiter.api.extension.ExtensionContext;

public class WiremockExtension implements Extension, BeforeAllCallback, AfterAllCallback {

    @Override
    public void beforeAll(ExtensionContext context) throws Exception {
        IntegrationTestBase.wiremock.start();
    }

    @Override
    public void afterAll(ExtensionContext context) throws Exception {
        IntegrationTestBase.wiremock.stop();
    }
}
