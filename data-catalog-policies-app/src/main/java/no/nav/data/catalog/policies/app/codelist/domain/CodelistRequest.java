package no.nav.data.catalog.policies.app.codelist.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CodelistRequest {
    private ListName listName;
    private String code;
    private String description;
}
