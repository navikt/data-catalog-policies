package no.nav.data.catalog.policies.app.common.nais;

import io.micrometer.core.instrument.MeterRegistry;
import no.nav.data.catalog.policies.app.AppStarter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(NaisEndpoints.class)
@ContextConfiguration(classes = AppStarter.class)
@ActiveProfiles("test")
public class NaisEndpointsTest {

    @MockBean
    private MeterRegistry meterRegistry;
    @MockBean
    private PolicyRepository repository;
    @Autowired
    private MockMvc mvc;

    @Test
    public void naisIsReady() throws Exception {
        String urlIsReady = "/internal/isReady";
        mvc.perform(get(urlIsReady))
                .andExpect(status().isOk());
    }

    @Test
    public void naisIsAlive() throws Exception {
        when(repository.count()).thenReturn(4L);
        String urlIsAlive = "/internal/isAlive";
        mvc.perform(get(urlIsAlive))
                .andExpect(status().isOk());
    }

    @Test
    public void naisIsDead() throws Exception {
        when(repository.count()).thenThrow(new InvalidDataAccessApiUsageException("permission denied"));
        String urlIsAlive = "/internal/isAlive";
        mvc.perform(get(urlIsAlive))
                .andExpect(status().isInternalServerError());
    }
}
