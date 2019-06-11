package no.nav.data.catalog.policies.test.integration.util;

import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class BackendResponses {
    public static void aktoerIdenterHappy() {
        stubFor(get("/backend/codelist")
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("")
                ));
    }
}
