package no.nav.data.catalog.policies.app.behandlingsgrunnlag;

import lombok.Value;
import no.nav.data.catalog.policies.app.policy.domain.DatasetResponse;

import java.util.List;

@Value
public class BehandlingsgrunnlagResponse {

    private String purpose;
    private List<DatasetResponse> datasets;
}
