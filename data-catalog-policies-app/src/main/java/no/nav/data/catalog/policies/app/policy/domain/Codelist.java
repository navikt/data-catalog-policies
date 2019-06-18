package no.nav.data.catalog.policies.app.policy.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public class Codelist {
    private final ListName listName;
    private final String code;
    private final String description;
}
