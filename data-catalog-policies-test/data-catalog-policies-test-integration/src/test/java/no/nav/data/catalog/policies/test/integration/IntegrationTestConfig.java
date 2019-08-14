package no.nav.data.catalog.policies.test.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import no.nav.data.catalog.policies.test.integration.util.WiremockResponseTransformer;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

public class IntegrationTestConfig {
    @Bean
    public WireMockServer wireMockServer() {
        WireMockServer wireMockServer = new WireMockServer(
                wireMockConfig().port(9089).extensions(WiremockResponseTransformer.class));

        wireMockServer.stubFor(get(urlMatching("/datacatalog/backend/codelist/(.*?)/NOTFOUND"))
                .atPriority(1)
                .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.TEXT_HTML_VALUE)
                ));
        wireMockServer.stubFor(get(urlMatching("/datacatalog/backend/codelist/(.*?)/(.*?)"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.TEXT_HTML_VALUE)
                        .withBody("description")
                ));
        wireMockServer.stubFor(get("/datacatalog/backend/dataset/title/Sivilstand")
                .atPriority(2)
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"elasticsearchId\":\"DSwO_moBkfggy-HvwyLZ\",\"datasetId\":\"0702e097-0800-47e1-9fc9-da9fa935c76d\",\"title\":\"Sivilstand\",\"description\":\"Sivilstand beskrivelse\",\"category\":{\"description\":\"Personalia\",\"code\":\"PERSONALIA\"},\"producer\":{\"description\":\"Folkeregisteret\",\"code\":\"FOLKEREGISTERET\"},\"system\":{\"description\":\"Tjenestebasert PersondataSystem\",\"code\":\"TPS\"},\"personalData\":true}")
                ));
        wireMockServer.stubFor(get("/datacatalog/backend/dataset/title/Postadresse")
                .atPriority(1)
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"elasticsearchId\":\"DSwO_moBkfggy-HvwyLZ\",\"datasetId\":\"bc83058f-4263-4d56-b26b-f5906e4b9339\",\"title\":\"Postadresse\",\"description\":\"Postadresse beskrivelse\",\"category\":{\"description\":\"Personalia\",\"code\":\"PERSONALIA\"},\"producer\":{\"description\":\"Folkeregisteret\",\"code\":\"FOLKEREGISTERET\"},\"system\":{\"description\":\"Tjenestebasert PersondataSystem\",\"code\":\"TPS\"},\"personalData\":true}")
                ));
        wireMockServer.stubFor(get("/datacatalog/backend/dataset/title/Arbeidsforhold")
                .atPriority(2)
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"elasticsearchId\":\"DSwO_moBkfggy-HvwyLZ\",\"datasetId\":\"b2766a0b-afaa-4572-a47f-90aff9c471a9\",\"title\":\"Arbeidsforhold\",\"description\":\"Arbeidsforhold beskrivelse\",\"category\":{\"description\":\"Personalia\",\"code\":\"PERSONALIA\"},\"producer\":{\"description\":\"Folkeregisteret\",\"code\":\"FOLKEREGISTERET\"},\"system\":{\"description\":\"Tjenestebasert PersondataSystem\",\"code\":\"TPS\"},\"personalData\":true}")
                ));
        wireMockServer.stubFor(get("/datacatalog/backend/dataset/title/NOTFOUND")
                .atPriority(1)
                .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("")
                ));
        wireMockServer.stubFor(get(urlMatching("/datacatalog/backend/dataset/.*?"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withTransformer("response-transformer", "", "")));


        return wireMockServer;
    }

    @PostConstruct
    public void start() {
        wireMockServer().start();
    }

    @PreDestroy
    public void stop() {
        wireMockServer().stop();
    }

}
