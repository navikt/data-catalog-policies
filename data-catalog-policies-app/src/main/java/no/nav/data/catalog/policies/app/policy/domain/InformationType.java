package no.nav.data.catalog.policies.app.policy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class InformationType {
    private Long id;
    private String name;
    private String description;
    private String categoryCode;
    private String producerCode;
    private String systemCode;
    private Boolean personalData;

    public InformationType convertToInformationType(InformationTypeResponse response) {
        this.id = response.getInformationTypeId();
        this.name = response.getName();
        this.description = response.getDescription();
        this.personalData = response.getPersonalData();
        this.categoryCode = response.getCategory().get("code").toString();
        this.producerCode = response.getProducer().get("code").toString();
        this.systemCode = response.getSystem().get("code").toString();
        return this;
    }
}
