package no.nav.data.catalog.policies.app.policy.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodelistRequest {
    private ListName listName;
    private String code;
    private String description;
}
