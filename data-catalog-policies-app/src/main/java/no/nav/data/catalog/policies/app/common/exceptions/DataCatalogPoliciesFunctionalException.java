package no.nav.data.catalog.policies.app.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generell exception for tekniske feil i DataCataogBackend.
 *
 * @author Ketill Fenne, Visma Consulting AS
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
@Getter
public class DataCatalogPoliciesFunctionalException extends RuntimeException {

    private final HttpStatus httpStatus;

    public DataCatalogPoliciesFunctionalException(String message) {
        super(message);
        this.httpStatus = HttpStatus.OK;
    }

    public DataCatalogPoliciesFunctionalException(String message, Throwable cause) {
        super(message, cause);
        this.httpStatus = HttpStatus.OK;
    }

    public DataCatalogPoliciesFunctionalException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
}
