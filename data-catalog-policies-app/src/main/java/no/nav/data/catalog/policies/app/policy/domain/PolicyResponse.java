package no.nav.data.catalog.policies.app.policy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyResponse {

    private Long policyId;
    private DatasetResponse dataset;
    private String legalBasisDescription;
    private CodeResponse purpose;
}
