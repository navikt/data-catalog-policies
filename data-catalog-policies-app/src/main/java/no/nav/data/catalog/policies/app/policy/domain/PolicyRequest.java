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
    @JsonIgnore
    private String datasetId;
    @JsonIgnore
    private Policy existingPolicy;

    public PolicyRequest(String legalBasisDescription, String purposeCode, String datasetTitle) {
        this.legalBasisDescription = legalBasisDescription;
        this.purposeCode = purposeCode;
        this.datasetTitle = datasetTitle;
    }
}
