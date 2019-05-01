package no.nav.data.catalog.policies.app.policy;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyRequest {
    private Long legalBasisId;
    private String legalBasisDescription;
    private Long purposeId;
    private Long informationTypeId;
}
