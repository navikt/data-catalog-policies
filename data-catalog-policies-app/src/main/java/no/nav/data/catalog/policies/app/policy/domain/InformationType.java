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
    private Long informationTypeId;
    private String name;

    public InformationType convertToInformationType(InformationTypeResponse response) {
        this.informationTypeId = response.getInformationTypeId();
        this.name = response.getName();
        return this;
    }
}
