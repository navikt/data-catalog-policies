package no.nav.data.catalog.policies.app.common.web;

import no.nav.data.catalog.policies.app.common.util.Constants;
import no.nav.data.catalog.policies.app.common.util.MdcUtils;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.util.Optional;

public class TraceHeaderRequestInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        String correlationId = MdcUtils.getOrGenerateCorrelationId();
        String callId = Optional.ofNullable(MdcUtils.getCallId()).orElse(correlationId);

        req.getHeaders().set(Constants.NAV_CORRELATION_ID, correlationId);
        req.getHeaders().set(Constants.NAV_CALL_ID, callId);
        req.getHeaders().set(Constants.NAV_CONSUMER_ID, Constants.APP_ID);
        return execution.execute(req, body);
    }
}