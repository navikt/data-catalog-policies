package no.nav.data.catalog.policies.app.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DataCatalogPoliciesTechnicalException extends RuntimeException {

    private final HttpStatus httpStatus;

    public DataCatalogPoliciesTechnicalException(String message) {
        super(message);
        this.httpStatus = HttpStatus.OK;
    }

    public DataCatalogPoliciesTechnicalException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.OK;
    }

    public DataCatalogPoliciesTechnicalException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
