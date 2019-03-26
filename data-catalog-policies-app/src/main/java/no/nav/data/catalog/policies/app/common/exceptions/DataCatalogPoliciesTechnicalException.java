package no.nav.data.catalog.policies.app.common.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Generell exception for tekniske feil i DataCataogBackend.
 *
 * @author Ketill Fenne, Visma Consulting AS
 */
@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
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
