package no.nav.data.catalog.policies.app.policy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InformationTypeRequest {
    private String name;
    private String categoryCode;
    private String producerCode;
    private String systemCode;
    private String description;
    private Boolean personalData;
}
