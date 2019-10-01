package no.nav.data.catalog.policies.app.policy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Dataset {

    private String datasetId;
    private String datasetTitle;

    public DatasetResponse convertToResponse() {
        return new DatasetResponse(getDatasetId(), getDatasetTitle());
    }
}
