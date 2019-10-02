package no.nav.data.catalog.policies.app.behandlingsgrunnlag;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

import java.util.List;

@Value
@JsonPropertyOrder({"purpose", "datasets"})
public class BehandlingsgrunnlagResponse {

    private String purpose;
    private List<DatasetBehandlingsgrunnlagResponse> datasets;
}
