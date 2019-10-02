package no.nav.data.catalog.policies.app.behandlingsgrunnlag;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Value;

@Value
@JsonPropertyOrder({"id", "title", "legalBasisDescription"})
public class DatasetBehandlingsgrunnlagResponse {

    private String id;
    private String title;
    private String legalBasisDescription;
}
