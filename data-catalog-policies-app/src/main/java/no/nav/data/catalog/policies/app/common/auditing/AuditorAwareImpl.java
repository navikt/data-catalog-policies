package no.nav.data.catalog.policies.app.common.auditing;

import no.nav.data.catalog.policies.app.common.util.MdcUtils;
import org.springframework.data.domain.AuditorAware;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {

    @Override
    public Optional<String> getCurrentAuditor() {
        return Optional.ofNullable(MdcUtils.getUser()).or(() -> Optional.of("no-user-set"));
    }
}
