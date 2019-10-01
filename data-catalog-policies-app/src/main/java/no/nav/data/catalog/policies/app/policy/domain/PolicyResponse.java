package no.nav.data.catalog.policies.app.policy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"policyId", "legalBasisDescription", "purpose", "dataset"})
public class PolicyResponse {

    private Long policyId;
    private DatasetResponse dataset;
    private String legalBasisDescription;
    private CodeResponse purpose;
}
