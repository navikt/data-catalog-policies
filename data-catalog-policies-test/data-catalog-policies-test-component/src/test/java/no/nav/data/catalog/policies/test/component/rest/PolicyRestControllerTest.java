package no.nav.data.catalog.policies.test.component.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.data.catalog.policies.app.AppStarter;
import no.nav.data.catalog.policies.app.policy.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.entities.InformationType;
import no.nav.data.catalog.policies.app.policy.entities.LegalBasis;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.entities.Purpose;
import no.nav.data.catalog.policies.app.policy.mapper.PolicyMapper;
import no.nav.data.catalog.policies.app.policy.repository.LegalBasisRepository;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import no.nav.data.catalog.policies.app.policy.repository.PurposeRepository;
import no.nav.data.catalog.policies.app.policy.rest.PolicyRestController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PolicyRestController.class)
@ContextConfiguration(classes = AppStarter.class)
@ActiveProfiles("test")
public class PolicyRestControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PolicyMapper mapper;

    @MockBean
    private PolicyRepository policyRepository;

    @MockBean
    private LegalBasisRepository legalBasisRepository;

    @MockBean
    private PurposeRepository purposeRepository;

    @Test
    public void getAllPolicies() throws Exception {
        Policy policy1 = createPolicyTestdata();
        Policy policy2 = createPolicyTestdata();

        List<Policy> policies = Arrays.asList(policy1, policy2);
        Page<Policy> policyPage = new PageImpl<>(policies);
        given(policyRepository.findAll(PageRequest.of(0, 100))).willReturn(policyPage);
        mvc.perform(get("/policy/policy?page=0&size=100").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(2)));
    }

    @Test
    public void getOnePolicy() throws Exception {
        Policy policy1 = createPolicyTestdata();

        given(policyRepository.findById(1L)).willReturn(Optional.of(policy1));
        mvc.perform(get("/policy/policy/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalBasisDescription",is("Description")));
    }

    @Test
    public void getNotExistingPolicy() throws Exception {
        given(policyRepository.findById(1L)).willReturn(Optional.ofNullable(null));
        mvc.perform(get("/policy/policy/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void createPolicy() throws Exception {
        Policy policy1 = createPolicyTestdata();
        PolicyRequest request = new PolicyRequest();

        given(mapper.mapRequestToPolicy(request, null)).willReturn(policy1);
        given(policyRepository.save(policy1)).willReturn(policy1);

        mvc.perform(post("/policy/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.legalBasisDescription",is("Description")));
    }

    @Test
    public void updatePolicy() throws Exception {
        Policy policy1 = createPolicyTestdata();
        PolicyRequest request = new PolicyRequest();

        given(mapper.mapRequestToPolicy(request, 1L)).willReturn(policy1);
        given(policyRepository.findById(1L)).willReturn(Optional.of(policy1));
        given(policyRepository.save(policy1)).willReturn(policy1);

        mvc.perform(put("/policy/policy/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalBasisDescription",is("Description")));
    }

    @Test
    public void updateNotExistingPolicy() throws Exception {
        Policy policy1 = createPolicyTestdata();
        PolicyRequest request = new PolicyRequest();

        given(mapper.mapRequestToPolicy(request, 1L)).willReturn(policy1);
        given(policyRepository.findById(1L)).willReturn(Optional.ofNullable(null));

        mvc.perform(put("/policy/policy/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletePolicy() throws Exception {
        mvc.perform(delete("/policy/policy/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteNotExistsingPolicy() throws Exception {
        doThrow(new EmptyResultDataAccessException(1)).when(policyRepository).deleteById(1L);
        mvc.perform(delete("/policy/policy/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    private Policy createPolicyTestdata() {
        Policy policy = new Policy();
        policy.setPurpose(new Purpose());
        policy.setLegalBasis(new LegalBasis());
        policy.setInformationType(new InformationType());
        policy.setLegalBasisDescription("Description");
        return  policy;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void getAllLegalBasis() throws Exception {
        LegalBasis lb1 = new LegalBasis();
        LegalBasis lb2 = new LegalBasis();

        List<LegalBasis> lbs = Arrays.asList(lb1, lb2);
        given(legalBasisRepository.findAll()).willReturn(lbs);
        mvc.perform(get("/policy/legalbasis").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }

    @Test
    public void getAllPurposes() throws Exception {
        Purpose p1 = new Purpose();
        Purpose p2 = new Purpose();

        List<Purpose> purposes = Arrays.asList(p1, p2);
        given(purposeRepository.findAll()).willReturn(purposes);
        mvc.perform(get("/policy/purpose").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$",hasSize(2)));
    }
}
