package no.nav.data.catalog.policies.app.policy.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.data.catalog.policies.app.policy.entities.Policy;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyRequest {

    private Long id;
    private String legalBasisDescription;
    private String purposeCode;
    private String datasetTitle;
    private String fom;
    private String tom;

    @JsonIgnore
    private String datasetId;
    @JsonIgnore
    private Policy existingPolicy;

}
