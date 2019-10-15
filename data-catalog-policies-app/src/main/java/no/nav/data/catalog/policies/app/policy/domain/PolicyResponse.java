package no.nav.data.catalog.policies.app.policy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.data.catalog.policies.app.codelist.domain.CodeResponse;
import no.nav.data.catalog.policies.app.dataset.domain.DatasetResponse;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"policyId", "legalBasisDescription", "fom", "tom", "active", "purpose", "dataset"})
public class PolicyResponse {

    private Long policyId;
    private String legalBasisDescription;
    private LocalDate fom;
    private LocalDate tom;
    private boolean active;
    private CodeResponse purpose;
    private DatasetResponse dataset;

}
