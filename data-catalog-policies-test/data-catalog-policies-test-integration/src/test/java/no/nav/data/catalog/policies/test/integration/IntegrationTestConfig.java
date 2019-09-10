package no.nav.data.catalog.policies.test.integration;

import com.github.tomakehurst.wiremock.http.ContentTypeHeader;
import com.github.tomakehurst.wiremock.junit.WireMockClassRule;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;

public class IntegrationTestConfig {

    public static void mockDataCatalogBackend(WireMockClassRule wiremock) {
        wiremock.stubFor(get(urlMatching("/backend/codelist/(.*?)/NOTFOUND"))
                .atPriority(1)
                .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.TEXT_HTML_VALUE)
                ));
        wiremock.stubFor(get(urlMatching("/backend/codelist/(.*?)/(.*?)"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.TEXT_HTML_VALUE)
                        .withBody("description")
                ));
        wiremock.stubFor(get("/backend/dataset/title/Sivilstand")
                .atPriority(2)
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                "{\"elasticsearchId\":\"DSwO_moBkfggy-HvwyLZ\",\"id\":\"0702e097-0800-47e1-9fc9-da9fa935c76d\",\"title\":\"Sivilstand\",\"description\":\"Sivilstand beskrivelse\",\"category\":{\"description\":\"Personalia\",\"code\":\"PERSONALIA\"},\"producer\":{\"description\":\"Folkeregisteret\",\"code\":\"FOLKEREGISTERET\"},\"system\":{\"description\":\"Tjenestebasert PersondataSystem\",\"code\":\"TPS\"},\"personalData\":true}")
                ));
        wiremock.stubFor(get("/backend/dataset/title/Postadresse")
                .atPriority(1)
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                "{\"elasticsearchId\":\"DSwO_moBkfggy-HvwyLZ\",\"id\":\"bc83058f-4263-4d56-b26b-f5906e4b9339\",\"title\":\"Postadresse\",\"description\":\"Postadresse beskrivelse\",\"category\":{\"description\":\"Personalia\",\"code\":\"PERSONALIA\"},\"producer\":{\"description\":\"Folkeregisteret\",\"code\":\"FOLKEREGISTERET\"},\"system\":{\"description\":\"Tjenestebasert PersondataSystem\",\"code\":\"TPS\"},\"personalData\":true}")
                ));
        wiremock.stubFor(get("/backend/dataset/title/Arbeidsforhold")
                .atPriority(2)
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody(
                                "{\"elasticsearchId\":\"DSwO_moBkfggy-HvwyLZ\",\"id\":\"b2766a0b-afaa-4572-a47f-90aff9c471a9\",\"title\":\"Arbeidsforhold\",\"description\":\"Arbeidsforhold beskrivelse\",\"category\":{\"description\":\"Personalia\",\"code\":\"PERSONALIA\"},\"producer\":{\"description\":\"Folkeregisteret\",\"code\":\"FOLKEREGISTERET\"},\"system\":{\"description\":\"Tjenestebasert PersondataSystem\",\"code\":\"TPS\"},\"personalData\":true}")
                ));
        wiremock.stubFor(get("/backend/dataset/title/NOTFOUND")
                .atPriority(1)
                .willReturn(aResponse().withStatus(HttpStatus.NOT_FOUND.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withBody("")
                ));
        wiremock.stubFor(get(urlMatching("/backend/dataset/.*?"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)
                        .withTransformer("response-transformer", "", "")));
        wiremock.stubFor(post(urlMatching("/backend/dataset/sync"))
                .willReturn(aResponse().withStatus(HttpStatus.OK.value())
                        .withHeader(ContentTypeHeader.KEY, MediaType.APPLICATION_JSON_VALUE)));
    }

}
