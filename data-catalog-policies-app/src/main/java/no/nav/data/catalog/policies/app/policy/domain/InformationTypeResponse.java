package no.nav.data.catalog.policies.app.policy.domain;

import lombok.Data;

import java.util.Map;

@Data
public class InformationTypeResponse {
    private String elasticsearchId;
    private Long informationTypeId;
    private String name;
    private String description;
    private Map category;
    private Map producer;
    private Map system;
    private Boolean personalData;
}
