package no.nav.data.catalog.policies.app.policy;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PolicyRequest {
    private Long legalBasisId;
    private Long purposeId;
    private Long informationTypeId;

    @JsonCreator
    public PolicyRequest(
            @JsonProperty(value = "legalBasisId", required = true) Long legalBasisId,
            @JsonProperty(value = "purposeId", required = true) Long purposeId,
            @JsonProperty(value = "informationTypeId", required = true) Long informationTypeId) {
        this.legalBasisId = legalBasisId;
        this.purposeId = purposeId;
        this.informationTypeId = informationTypeId;
    }
}
