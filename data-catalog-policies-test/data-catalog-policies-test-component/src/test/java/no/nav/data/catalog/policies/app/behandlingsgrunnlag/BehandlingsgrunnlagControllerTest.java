package no.nav.data.catalog.policies.app.behandlingsgrunnlag;

import no.nav.data.catalog.policies.app.AppStarter;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@ExtendWith(SpringExtension.class)
@WebMvcTest(BehandlingsgrunnlagController.class)
@ContextConfiguration(classes = AppStarter.class)
@ActiveProfiles("test")
class BehandlingsgrunnlagControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PolicyRepository policyRepository;

    @Test
    void hentBehandlingsgrunnlag() throws Exception {
        given(policyRepository.findByPurposeCode("the-purpose"))
                .willReturn(List.of(Policy.builder().datasetId("id").datasetTitle("title").legalBasisDescription("legaldesc").build()));

        mvc.perform(get("/behandlingsgrunnlag/purpose/{purpose}", "the-purpose"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.purpose", is("the-purpose")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.datasets[0].id", is("id")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.datasets[0].title", is("title")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.datasets[0].legalBasisDescription", is("legaldesc")));
    }
}