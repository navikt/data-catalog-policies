package no.nav.data.catalog.policies.app.common.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.HashMap;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends RuntimeException {
    private HashMap<String, String> validationErrors;

    public ValidationException(HashMap<String, String> validationErrors) {
        this.validationErrors = validationErrors;
    }

    public ValidationException(HashMap<String, String> validationErrors, String message) {
        super(message + " " + validationErrors);
        this.validationErrors = validationErrors;
    }

    public HashMap<String, String> get() {
        return validationErrors;
    }
}
