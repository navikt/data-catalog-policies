package no.nav.data.catalog.policies.app.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class DataCatalogPoliciesNotFoundException extends RuntimeException {
    public DataCatalogPoliciesNotFoundException(String message) {
        super(message);
    }
}
