package no.nav.data.catalog.policies.app.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DataCatalogPoliciesFunctionalException extends RuntimeException {

    private final HttpStatus httpStatus;

    public DataCatalogPoliciesFunctionalException(String message) {
        super(message);
        this.httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
