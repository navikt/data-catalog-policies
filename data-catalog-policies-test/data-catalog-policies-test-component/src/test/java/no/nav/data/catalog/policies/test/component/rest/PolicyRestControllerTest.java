package no.nav.data.catalog.policies.test.component.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.data.catalog.policies.app.AppStarter;
import no.nav.data.catalog.policies.app.consumer.DatasetConsumer;
import no.nav.data.catalog.policies.app.policy.PolicyService;
import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.domain.PolicyResponse;
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
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@WebMvcTest(PolicyRestController.class)
@ContextConfiguration(classes = AppStarter.class)
@ActiveProfiles("test")
public class PolicyRestControllerTest {

    private static final String DATASET_ID_1 = "cd7f037e-374e-4e68-b705-55b61966b2fc";
    private static final String DATASET_ID_2 = "5992e0d0-1fc9-4d67-b825-d198be0827bf";

    @Autowired
    private MockMvc mvc;

    @MockBean
    private PolicyMapper mapper;

    @MockBean
    private PolicyService service;

    @MockBean
    private PolicyRepository policyRepository;

    @MockBean
    private DatasetConsumer datasetConsumer;

    @Test
    public void getAllPolicies() throws Exception {
        Policy policy1 = createPolicyTestdata(DATASET_ID_1);
        Policy policy2 = createPolicyTestdata(DATASET_ID_2);

        List<Policy> policies = Arrays.asList(policy1, policy2);
        Page<Policy> policyPage = new PageImpl<>(policies);
        given(policyRepository.findAll(PageRequest.of(0, 100))).willReturn(policyPage);
        mvc.perform(get("/policy?pageNumber=0&pageSize=100")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

    @Test
    public void getOnePolicy() throws Exception {
        Policy policy1 = createPolicyTestdata(DATASET_ID_1);
        PolicyResponse response = new PolicyResponse(1L, new Dataset(), "Description", null);

        given(policyRepository.findById(1L)).willReturn(Optional.of(policy1));
        given(mapper.mapPolicyToResponse(policy1)).willReturn(response);
        ResultActions result = mvc.perform(get("/policy/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalBasisDescription", is("Description")));
    }

    @Test
    public void getNotExistingPolicy() throws Exception {
        given(policyRepository.findById(1L)).willReturn(Optional.ofNullable(null));
        mvc.perform(get("/policy/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    public void getPoliciesForDataset() throws Exception {
        Policy policy1 = createPolicyTestdata(DATASET_ID_1);

        List<Policy> policies = Collections.singletonList(policy1);
        Page<Policy> policyPage = new PageImpl<>(policies);
        given(policyRepository.findByDatasetId(PageRequest.of(0, 100), DATASET_ID_1)).willReturn(policyPage);
        mvc.perform(get("/policy?pageNumber=0&pageSize=100&datasetId=" + DATASET_ID_1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(1)));

    }

    @Test
    public void countPoliciesForDataset() throws Exception {
        given(policyRepository.countByDatasetId(DATASET_ID_1)).willReturn(1L);
        mvc.perform(get("/policy/count?datasetId=" + DATASET_ID_1).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }

    @Test
    public void countPolicies() throws Exception {
        given(policyRepository.count()).willReturn(1L);
        mvc.perform(get("/policy/count").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string("1"));
    }


    @Test
    public void createOnePolicy() throws Exception {
        Policy policy1 = createPolicyTestdata(DATASET_ID_1);
        List<PolicyRequest> request = Collections.singletonList(new PolicyRequest());
        List<Policy> policies = Collections.singletonList(policy1);

        given(mapper.mapRequestToPolicy(request.get(0), null)).willReturn(policy1);
        given(policyRepository.saveAll(policies)).willReturn(policies);

        mvc.perform(post("/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.*", hasSize(1)));

        verify(datasetConsumer).syncDatasetById(List.of(policy1.getDatasetId()));
    }

    @Test
    public void createTwoPolicies() throws Exception {
        Policy policy1 = createPolicyTestdata(DATASET_ID_1);
        Policy policy2 = createPolicyTestdata(DATASET_ID_2);
        List<PolicyRequest> request = Arrays.asList(new PolicyRequest("Desc1", "Code1", "Title1"), new PolicyRequest("Desc2", "Code2", "Title2"));
        List<Policy> policies = Arrays.asList(policy1, policy2);

        given(mapper.mapRequestToPolicy(request.get(0), null)).willReturn(policy1);
        given(mapper.mapRequestToPolicy(request.get(1), null)).willReturn(policy2);
        given(policyRepository.saveAll(policies)).willReturn(policies);

        mvc.perform(post("/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.*", hasSize(2)));
        verify(datasetConsumer).syncDatasetById(List.of(policy1.getDatasetId(), policy2.getDatasetId()));
    }

    @Test
    public void updatePolicy() throws Exception {
        Policy policy1 = createPolicyTestdata(DATASET_ID_1);
        PolicyRequest request = new PolicyRequest();
        PolicyResponse response = new PolicyResponse(1L, new Dataset(), "Description", null);

        given(mapper.mapRequestToPolicy(request, 1L)).willReturn(policy1);
        given(policyRepository.findById(1L)).willReturn(Optional.of(policy1));
        given(policyRepository.save(policy1)).willReturn(policy1);
        given(mapper.mapPolicyToResponse(policy1)).willReturn(response);

        mvc.perform(put("/policy/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.legalBasisDescription", is("Description")));

        verify(datasetConsumer).syncDatasetById(List.of(policy1.getDatasetId()));
    }

    @Test
    public void updateTwoPolicies() throws Exception {
        Policy policy1 = createPolicyTestdata(DATASET_ID_1);
        Policy policy2 = createPolicyTestdata(DATASET_ID_2);
        List<PolicyRequest> request = Arrays.asList(new PolicyRequest("Desc1", "Code1", "Title1"), new PolicyRequest("Desc2", "Code2", "Title2"));
        List<Policy> policies = Arrays.asList(policy1, policy2);

        given(mapper.mapRequestToPolicy(request.get(0), request.get(0).getId())).willReturn(policy1);
        given(mapper.mapRequestToPolicy(request.get(1), request.get(1).getId())).willReturn(policy2);
        given(policyRepository.findById(request.get(0).getId())).willReturn(Optional.of(policy1));
        given(policyRepository.findById(request.get(1).getId())).willReturn(Optional.of(policy2));
        given(policyRepository.saveAll(policies)).willReturn(policies);

        mvc.perform(put("/policy")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.*", hasSize(2)));

        verify(datasetConsumer).syncDatasetById(List.of(policy1.getDatasetId(), policy2.getDatasetId()));
    }

    @Test
    public void updateNotExistingPolicy() throws Exception {
        Policy policy1 = createPolicyTestdata(DATASET_ID_1);
        PolicyRequest request = new PolicyRequest();

        given(mapper.mapRequestToPolicy(request, 1L)).willReturn(policy1);
        given(policyRepository.findById(1L)).willReturn(Optional.ofNullable(null));

        mvc.perform(put("/policy/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(request)))
                .andExpect(status().isNotFound());
    }

    @Test
    public void deletePolicy() throws Exception {
        Policy policy1 = createPolicyTestdata(DATASET_ID_1);
        given(policyRepository.findById(1L)).willReturn(Optional.of(policy1));

        mvc.perform(delete("/policy/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(datasetConsumer).syncDatasetById(List.of(policy1.getDatasetId()));
    }

    @Test
    public void deleteNotExistsingPolicy() throws Exception {
        doThrow(new EmptyResultDataAccessException(1)).when(policyRepository).deleteById(1L);
        mvc.perform(delete("/policy/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    private Policy createPolicyTestdata(String datasetId) {
        Policy policy = new Policy();
        Dataset dataset = new Dataset();
        dataset.setId(datasetId);
        policy.setDatasetId(dataset.getId());
        policy.setLegalBasisDescription("Description");
        return policy;
    }

    private static String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
