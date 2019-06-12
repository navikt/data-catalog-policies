package no.nav.data.catalog.policies.test.integration.util;

import com.github.tomakehurst.wiremock.common.FileSource;
import com.github.tomakehurst.wiremock.extension.Parameters;
import com.github.tomakehurst.wiremock.extension.ResponseTransformer;
import com.github.tomakehurst.wiremock.http.Request;
import com.github.tomakehurst.wiremock.http.Response;

public class WiremockResponseTransformer extends ResponseTransformer {

    @Override
    public Response transform(Request request, Response response, FileSource files, Parameters parameters) {
        if (response.getBody().length == 0) {
            return Response.Builder.like(response)
                    .but().body("{\"elasticsearchId\":\"DSwO_moBkfggy-HvwyLZ\",\"informationTypeId\":" + request.getUrl().substring(request.getUrl().lastIndexOf("/") + 1) + ",\"name\":\"Sivilstand\",\"description\":\"Sivilstand beskrivelse\",\"category\":{\"description\":\"Personalia\",\"code\":\"PERSONALIA\"},\"producer\":{\"description\":\"Folkeregisteret\",\"code\":\"FOLKEREGISTERET\"},\"system\":{\"description\":\"Tjenestebasert PersondataSystem\",\"code\":\"TPS\"},\"personalData\":true}")
                    .build();
        } else
            return response;
    }

    @Override
    public String getName() {
        return "response-transformer";
    }
}