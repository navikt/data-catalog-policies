package no.nav.data.catalog.policies.app.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyRequest {
    private Long legalBasisId;
    private String legalBasisDescription;
    private Long purposeId;
    private Long informationTypeId;

    @JsonCreator
    public PolicyRequest(
            @JsonProperty(value = "legalBasisId", required = true) Long legalBasisId,
            @JsonProperty(value = "legalBasisDescription", required = true) String legalBasisDescription,
            @JsonProperty(value = "purposeId", required = true) Long purposeId,
            @JsonProperty(value = "informationTypeId", required = true) Long informationTypeId) {
        this.legalBasisId = legalBasisId;
        this.purposeId = purposeId;
        this.informationTypeId = informationTypeId;
        this.legalBasisDescription = legalBasisDescription;
    }
}
