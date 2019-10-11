package no.nav.data.catalog.policies.app.common.util;

public final class Constants {

    private Constants() {
        throw new IllegalStateException("Utility class");
    }

    /* Header names */
    // unique id set by caller
    public static final String NAV_CALL_ID = "Nav-Call-Id";
    // unique id set by this application
    public static final String NAV_CORRELATION_ID = "Nav-Correlation-Id";
    // application id set by caller
    public static final String NAV_CONSUMER_ID = "Nav-Consumer-Id";

    public static final String APP_ID = "data-catalog-policies";
}
