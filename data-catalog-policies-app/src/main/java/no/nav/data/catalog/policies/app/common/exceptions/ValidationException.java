package no.nav.data.catalog.policies.app.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Collections;
import java.util.Map;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {

    private final Map<String, Map<String, String>> validationErrors;

    public ValidationException(String message) {
        this(Collections.emptyMap(), message);
    }

    public ValidationException(Map<String, Map<String, String>> validationErrors, String message) {
        super(message + " " + validationErrors);
        this.validationErrors = validationErrors;
    }

    public Map<String, Map<String, String>> get() {
        return validationErrors;
    }
}
