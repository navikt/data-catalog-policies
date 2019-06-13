package no.nav.data.catalog.policies.test.component.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.data.catalog.policies.app.AppStarter;
import no.nav.data.catalog.policies.app.policy.domain.InformationType;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.domain.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.PolicyService;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.mapper.PolicyMapper;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
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
import org.springframework.test.web.servlet.ResultActions;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
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
    private PolicyService service;

    @MockBean
    private PolicyRepository policyRepository;

    @Test
    public void getAllPolicies() throws Exception {
        Policy policy1 = createPolicyTestdata(1L);
        Policy policy2 = createPolicyTestdata(2L);

        List<Policy> policies = Arrays.asList(policy1, policy2);
        Page<Policy> policyPage = new PageImpl<>(policies);
        given(policyRepository.findAll(PageRequest.of(0, 100))).willReturn(policyPage);
        mvc.perform(get("/policy/policy?page=0&size=100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(2)));
    }

    @Test
    public void getOnePolicy() throws Exception {
        Policy policy1 = createPolicyTestdata(1L);
        PolicyResponse response = new PolicyResponse(1L, new InformationType(), "Description", null);

        given(policyRepository.findById(1L)).willReturn(Optional.of(policy1));
        given(mapper.mapPolicyToResponse(policy1)).willReturn(response);
        ResultActions result = mvc.perform(get("/policy/policy/1").contentType(MediaType.APPLICATION_JSON))
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
    public void getPoliciesForInformationType()throws Exception {
        Policy policy1 = createPolicyTestdata(1L);

        List<Policy> policies = Arrays.asList(policy1);
        Page<Policy> policyPage = new PageImpl<>(policies);
        given(policyRepository.findByInformationTypeId(PageRequest.of(0, 100), 1L)).willReturn(policyPage);
        mvc.perform(get("/policy/policy?page=0&size=100&informationTypeId=1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content",hasSize(1)));

    }

    @Test
    public void createOnePolicy() throws Exception {
        Policy policy1 = createPolicyTestdata(1L);
        List<PolicyRequest> request = Arrays.asList(new PolicyRequest());
        List<Policy> policies = Arrays.asList(policy1);

        given(mapper.mapRequestToPolicy(request.get(0), null)).willReturn(policy1);
        given(policyRepository.saveAll(policies)).willReturn(policies);

        mvc.perform(post("/policy/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.*",hasSize(1)));
    }

    @Test
    public void createTwoPolicies() throws Exception {
        Policy policy1 = createPolicyTestdata(1L);
        Policy policy2 = createPolicyTestdata(2L);
        List<PolicyRequest> request = Arrays.asList(new PolicyRequest("Desc1","Code1","Name1"), new PolicyRequest("Desc2","Code2","Name2"));
        List<Policy> policies = Arrays.asList(policy1, policy2);

        given(mapper.mapRequestToPolicy(request.get(0), null)).willReturn(policy1);
        given(mapper.mapRequestToPolicy(request.get(1), null)).willReturn(policy2);
        given(policyRepository.saveAll(policies)).willReturn(policies);

        mvc.perform(post("/policy/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.*",hasSize(2)));
    }

    @Test
    public void updatePolicy() throws Exception {
        Policy policy1 = createPolicyTestdata(1L);
        PolicyRequest request = new PolicyRequest();
        PolicyResponse response = new PolicyResponse(1L, new InformationType(), "Description", null);

        given(mapper.mapRequestToPolicy(request, 1L)).willReturn(policy1);
        given(policyRepository.findById(1L)).willReturn(Optional.of(policy1));
        given(policyRepository.save(policy1)).willReturn(policy1);
        given(mapper.mapPolicyToResponse(policy1)).willReturn(response);

        mvc.perform(put("/policy/policy/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalBasisDescription",is("Description")));
    }

    @Test
    public void updateTwoPolicies() throws Exception {
        Policy policy1 = createPolicyTestdata(1L);
        Policy policy2 = createPolicyTestdata(2L);
        List<PolicyRequest> request = Arrays.asList(new PolicyRequest("Desc1","Code1","Name1"), new PolicyRequest("Desc2","Code2","Name2"));
        List<Policy> policies = Arrays.asList(policy1, policy2);

        given(mapper.mapRequestToPolicy(request.get(0), request.get(0).getId())).willReturn(policy1);
        given(mapper.mapRequestToPolicy(request.get(1), request.get(1).getId())).willReturn(policy2);
        given(policyRepository.findById(request.get(0).getId())).willReturn(Optional.of(policy1));
        given(policyRepository.findById(request.get(1).getId())).willReturn(Optional.of(policy2));
        given(policyRepository.saveAll(policies)).willReturn(policies);

        mvc.perform(put("/policy/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*",hasSize(2)));
    }

    @Test
    public void updateNotExistingPolicy() throws Exception {
        Policy policy1 = createPolicyTestdata(1L);
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
        Policy policy1 = createPolicyTestdata(1L);
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


    private Policy createPolicyTestdata(Long informationTypeId) {
        Policy policy = new Policy();
        InformationType informationType = new InformationType();
        informationType.setId(informationTypeId);
        policy.setInformationTypeId(informationType.getId());
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
}
