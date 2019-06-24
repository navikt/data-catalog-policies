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

        wireMockServer.stubFor(get(urlMatching("/backend/codelist/(.*?)/NOTFOUND"))
                .atPriority(1)
                .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.TEXT_HTML_VALUE)
                ));
        wireMockServer.stubFor(get(urlMatching("/backend/codelist/(.*?)/(.*?)"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.TEXT_HTML_VALUE)
                        .withBody("description")
                ));
        wireMockServer.stubFor(get("/backend/informationtype/name/Sivilstand")
                .atPriority(2)
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"elasticsearchId\":\"DSwO_moBkfggy-HvwyLZ\",\"informationTypeId\":1,\"name\":\"Sivilstand\",\"description\":\"Sivilstand beskrivelse\",\"category\":{\"description\":\"Personalia\",\"code\":\"PERSONALIA\"},\"producer\":{\"description\":\"Folkeregisteret\",\"code\":\"FOLKEREGISTERET\"},\"system\":{\"description\":\"Tjenestebasert PersondataSystem\",\"code\":\"TPS\"},\"personalData\":true}")
                ));
        wireMockServer.stubFor(get("/backend/informationtype/name/Postadresse")
                .atPriority(1)
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"elasticsearchId\":\"DSwO_moBkfggy-HvwyLZ\",\"informationTypeId\":2,\"name\":\"Postadresse\",\"description\":\"Postadresse beskrivelse\",\"category\":{\"description\":\"Personalia\",\"code\":\"PERSONALIA\"},\"producer\":{\"description\":\"Folkeregisteret\",\"code\":\"FOLKEREGISTERET\"},\"system\":{\"description\":\"Tjenestebasert PersondataSystem\",\"code\":\"TPS\"},\"personalData\":true}")
                ));
        wireMockServer.stubFor(get("/backend/informationtype/name/Arbeidsforhold")
                .atPriority(2)
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("{\"elasticsearchId\":\"DSwO_moBkfggy-HvwyLZ\",\"informationTypeId\":3,\"name\":\"Arbeidsforhold\",\"description\":\"Arbeidsforhold beskrivelse\",\"category\":{\"description\":\"Personalia\",\"code\":\"PERSONALIA\"},\"producer\":{\"description\":\"Folkeregisteret\",\"code\":\"FOLKEREGISTERET\"},\"system\":{\"description\":\"Tjenestebasert PersondataSystem\",\"code\":\"TPS\"},\"personalData\":true}")
                ));
        wireMockServer.stubFor(get("/backend/informationtype/name/NOTFOUND")
                .atPriority(1)
                .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("")
                ));
        wireMockServer.stubFor(get(urlMatching("/backend/informationtype/.*?"))
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
