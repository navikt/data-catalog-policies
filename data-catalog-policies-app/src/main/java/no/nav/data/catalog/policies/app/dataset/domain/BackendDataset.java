package no.nav.data.catalog.policies.app.dataset.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BackendDataset {

    private String id;
    private String title;

    public Dataset convertToDataset() {
        return new Dataset(id, title);
    }
}
