package no.nav.data.catalog.policies.app.policy.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Dataset {

    private String id;
    private String title;

    public Dataset convertToDataset(DatasetResponse response) {
        this.id = response.getId();
        this.title = response.getTitle();
        return this;
    }
}
