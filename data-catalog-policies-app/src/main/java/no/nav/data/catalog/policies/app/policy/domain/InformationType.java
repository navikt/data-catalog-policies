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

    public InformationType convertToInformationType(InformationTypeResponse response) {
        this.id = response.getInformationTypeId();
        this.name = response.getName();
        return this;
    }
}
