package no.nav.data.catalog.policies.app.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class CodelistConsumer {
    @Autowired
    private RestTemplate restTemplate;

    @Value("${datacatalog.codelist.purpose.url}")
    private String purposeCodelistUrl;

    public String getPurposeCodelistDescription(String purposeCode) {
        ResponseEntity responseEntity = restTemplate.getForEntity(purposeCodelistUrl + "/" + purposeCode, String.class);
        return responseEntity.getBody().toString();
    }
}
