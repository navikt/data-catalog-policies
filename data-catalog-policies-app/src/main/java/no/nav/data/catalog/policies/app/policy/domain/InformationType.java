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

    public InformationType convertToInformationType(InformationTypeResponse response) {
        this.id = response.getInformationTypeId();
        this.name = response.getName();
        this.description = response.getDescription();
        return this;
    }

    public InformationTypeRequest convertToInformationTypeRequest() {
        InformationTypeRequest request = new InformationTypeRequest();
        request.setName(this.name);
        request.setPersonalData(true);
        request.setDescription(this.description);
        request.setCategoryCode("");
        request.setProducerCode("");
        request.setSystemCode("");
        return request;
    }


}
