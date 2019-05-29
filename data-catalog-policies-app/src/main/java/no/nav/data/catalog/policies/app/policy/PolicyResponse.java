package no.nav.data.catalog.policies.app.policy;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.data.catalog.policies.app.policy.entities.InformationType;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyResponse {
    private Long policyId;
    private InformationType informationType;
    private String legalBasisDescription;
    private Map<String, String> purpose;
}
