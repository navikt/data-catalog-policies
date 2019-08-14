package no.nav.data.catalog.policies.app.policy.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.policy.PolicyService;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.domain.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.mapper.PolicyMapper;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static no.nav.data.catalog.policies.app.common.cache.CacheConfig.*;

@RestController
@CrossOrigin
@Api(value = "Data Catalog Policies", description = "REST API for Policies", tags = {"Policies"})
@RequestMapping("/policy")
@Slf4j
public class PolicyRestController {

    @Autowired
    private PolicyService service;

    @Autowired
    private PolicyMapper mapper;

    @Autowired
    private PolicyRepository policyRepository;

    @Autowired
    private CacheManager cachemanager;


    @ApiOperation(value = "Get all Policies", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All policies fetched", response = Policy.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/policy")
    public RestResponsePage<PolicyResponse> getPolicies(Pageable pageable) {
        log.debug("Received request for all Policies");
        Page<PolicyResponse>  policyResponses = policyRepository.findAll(pageable).map(policy -> mapper.mapPolicyToResponse(policy));
        return new RestResponsePage<>(policyResponses.getContent(), pageable, policyResponses.getTotalElements());
    }

    @ApiOperation(value = "Count all Policies", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Count policies fetched", response =  Long.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/policy/count")
    public Long countPolicies() {
        log.debug("Received request for number of Policies");
        return policyRepository.count();
    }

    @ApiOperation(value = "Get all Policies related to Dataset", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All policies fetched", response = Policy.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping(path = "/policy", params = {"datasetId"}, produces = "application/json")
    public RestResponsePage<PolicyResponse> getPoliciesByDataset(Pageable pageable, @RequestParam UUID datasetId) {
        log.debug("Received request for Policies related to Dataset with id={}", datasetId);
        if (pageable.getSort().getOrderFor("purpose.description") != null) {
            List<PolicyResponse> pageResponse = policyRepository.findByDatasetId(null, datasetId).stream().map(policy -> mapper.mapPolicyToResponse(policy)).collect(Collectors.toList());
            Comparator<PolicyResponse> compareByDescription = Comparator.comparing((PolicyResponse o) -> o.getPurpose().get("description"));
            if (pageable.getSort().getOrderFor("purpose.description").isAscending()) {
                pageResponse.sort(compareByDescription);
            } else {
                pageResponse.sort(compareByDescription.reversed());
            }
            return new RestResponsePage<>(pageResponse, pageable, pageResponse.size());
        } else {
            Page<PolicyResponse> policyResponses = policyRepository.findByDatasetId(pageable, datasetId).map(policy -> mapper.mapPolicyToResponse(policy));
            return new RestResponsePage<>(policyResponses.getContent(), pageable, policyResponses.getTotalElements());
        }
    }

    @ApiOperation(value = "Count Policies by Dataset", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Count fetched", response = Long.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping(path = "/policy/count", params = {"datasetId"})
    public Long countPoliciesByDataset(@RequestParam UUID datasetId) {
        log.debug("Received request for number of policies related to Datasets with id={}", datasetId);
        return policyRepository.countByDatasetId(datasetId);
    }

    @ApiOperation(value = "Create Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Policy successfully created", response = Policy.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Illegal arguments"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PostMapping("/policy")
    @ResponseStatus(HttpStatus.CREATED)
    public List<PolicyResponse> createPolicy(@Valid @RequestBody List<PolicyRequest> policyRequests) {
        log.debug("Received request to create Policies");
        service.validateRequests(policyRequests);
        List<Policy> policies = policyRequests.stream().map(policy -> mapper.mapRequestToPolicy(policy, null)).collect(toList());
        return policyRepository.saveAll(policies).stream().map(policy -> mapper.mapPolicyToResponse(policy)).collect(Collectors.toList());
    }

    @ApiOperation(value = "Get Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Fetched policy", response = Policy.class),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/policy/{id}")
    public PolicyResponse getPolicy(@PathVariable Long id) {
        log.debug("Received request for Policy with id={}", id);
        Optional<Policy> optionalPolicy = policyRepository.findById(id);
        if (optionalPolicy.isEmpty()) {
            log.error("getPolicy: Cannot find Policy with id: {}", id);
            throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find Policy with id: %s", id));
        }
        return mapper.mapPolicyToResponse(optionalPolicy.get());
    }

    @ApiOperation(value = "Delete Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Policy deleted"),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @DeleteMapping("/policy/{id}")
    public void deletePolicy(@PathVariable Long id) {
        log.debug("Received request to delete Policy with id={}", id);
        try {
            policyRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            log.error(String.format("deletePolicy: Finner ikke id: %s, som skal slettes", id), e);
            throw new DataCatalogPoliciesNotFoundException(e.getMessage());
        }
    }

    @ApiOperation(value = "Update Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Policy updated"),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PutMapping("/policy/{id}")
    public PolicyResponse updatePolicy(@PathVariable Long id, @Valid @RequestBody PolicyRequest policyRequest) {
        log.debug("Received request to update Policy with id={}", id);
        service.validateRequests(List.of(policyRequest));
        Optional<Policy> optionalPolicy = policyRepository.findById(id);
        if (optionalPolicy.isEmpty()) {
            log.error(String.format("Cannot find Policy with id: %s", id));
            throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find Policy with id: %s", id));
        }
        Policy storedPolicy = optionalPolicy.get();
        Policy policy = mapper.mapRequestToPolicy(policyRequest, id);
        policy.setCreatedBy(storedPolicy.getCreatedBy());
        policy.setCreatedDate(storedPolicy.getCreatedDate());
        return mapper.mapPolicyToResponse(policyRepository.save(policy));
    }

    @ApiOperation(value = "Update Policies", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Polices updated", response = Policy.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PutMapping("/policy")
    public List<PolicyResponse> updatePolicies(@Valid @RequestBody List<PolicyRequest> policyRequests) {
        log.debug("Received requests to update Policies");
        service.validateRequests(policyRequests);
        List<Policy> policies = new ArrayList<>();
        policyRequests.forEach(policyRequest -> {
                    Optional<Policy> optionalPolicy = policyRepository.findById(policyRequest.getId());
                    if (optionalPolicy.isEmpty()) {
                        log.error(String.format("Cannot find Policy with id: %s", policyRequest.getId()));
                        throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find Policy with id: %s", policyRequest.getId()));
                    }
                    Policy storedPolicy = optionalPolicy.get();
                    Policy policy = mapper.mapRequestToPolicy(policyRequest, policyRequest.getId());
                    policy.setCreatedBy(storedPolicy.getCreatedBy());
                    policy.setCreatedDate(storedPolicy.getCreatedDate());
                    policies.add(policy);
                }
        );
        return policyRepository.saveAll(policies).stream().map(policy -> mapper.mapPolicyToResponse(policy)).collect(Collectors.toList());
    }

    @ApiOperation(value = "Cache evict", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cache evict", response = String.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/clearcache")
    public String clearCache() {
        cachemanager.getCache(CODELIST_CACHE).clear();
        cachemanager.getCache(DATASET_BY_TITLE_CACHE).clear();
        cachemanager.getCache(DATASET_BY_ID_CACHE).clear();
        return "OK";
    }
}
