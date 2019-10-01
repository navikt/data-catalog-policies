package no.nav.data.catalog.policies.app.policy.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder({"id", "title"})
public class DatasetResponse {

    private String id;
    private String title;

    public static DatasetResponse convertToDataset(BackendDataset response) {
        return new DatasetResponse(response.getId(), response.getTitle());
    }
}
