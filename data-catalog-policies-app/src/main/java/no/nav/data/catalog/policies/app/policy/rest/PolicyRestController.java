package no.nav.data.catalog.policies.app.policy.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.policy.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.mapper.PolicyMapper;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@CrossOrigin
@Api(value = "Data Catalog Policies", description = "REST API for Policies", tags = {"Policies"})
@RequestMapping("/policy")
@Slf4j
public class PolicyRestController {
    private static final Logger logger = LoggerFactory.getLogger(PolicyRestController.class);

    @Autowired
    private PolicyMapper mapper;

    @Autowired
    private PolicyRepository policyRepository;

    @ApiOperation(value = "Get all Policies", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All policies fetched", response = Policy.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/policy")
    public Page<Policy> getPolicies(Pageable pageable) {
        return policyRepository.findAll(pageable);
    }

    @ApiOperation(value = "Create Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Policy successfully created", response = Policy.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Illegal arguments"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PostMapping("/policy")
    @ResponseStatus(HttpStatus.CREATED)
    public List<Policy> createPolicy(@Valid @RequestBody List<PolicyRequest> policyRequests) {
        List<Policy> policies = policyRequests.stream().map(policy -> mapper.mapRequestToPolicy(policy, null)).collect(Collectors.toList());
        return policyRepository.saveAll(policies);
    }

    @ApiOperation(value = "Get Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Fetched policy", response = Policy.class),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/policy/{id}")
    public Policy getPolicy(@PathVariable Long id) {
        Optional<Policy> optionalPolicy = policyRepository.findById(id);
        if (!optionalPolicy.isPresent()) {
            logger.error(String.format("getPolicy: Cannot find Policy with id: %s", id));
            throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find Policy with id: %s", id));
        }
        return optionalPolicy.get();
    }

    @ApiOperation(value = "Delete Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Policy deleted"),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @DeleteMapping("/policy/{id}")
    public void deletePolicy(@PathVariable Long id) {
        try {
            policyRepository.deleteById(id);
        } catch (EmptyResultDataAccessException e) {
            logger.error(String.format("deletePolicy: Finner ikke id: %s, som skal slettes", id), e);
            throw new DataCatalogPoliciesNotFoundException(e.getMessage());
        }
    }

    @ApiOperation(value = "Update Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Policy updated"),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PutMapping("/policy/{id}")
    public Policy updatePolicy(@PathVariable Long id, @Valid @RequestBody PolicyRequest policyRequest) {
        Optional<Policy> optionalPolicy = policyRepository.findById(id);
        if (!optionalPolicy.isPresent()) {
            throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find Policy with id: %s", id));
        }
        Policy storedPolicy = optionalPolicy.get();
        Policy policy = mapper.mapRequestToPolicy(policyRequest, id);
        policy.setCreatedBy(storedPolicy.getCreatedBy());
        policy.setCreatedDate(storedPolicy.getCreatedDate());
        return policyRepository.save(policy);
    }

    @ApiOperation(value = "Update Policies", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Polices updated", response = Policy.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PutMapping("/policy")
    public List<Policy> updatePolicies(@Valid @RequestBody List<PolicyRequest> policyRequests) {
        List<Policy> policies = new ArrayList<>();
        policyRequests.forEach(policyRequest -> {
                    Optional<Policy> optionalPolicy = policyRepository.findById(policyRequest.getId());
                    if (!optionalPolicy.isPresent()) {
                        throw new DataCatalogPoliciesNotFoundException(String.format("Cannot find Policy with id: %s", policyRequest.getId()));
                    }
                    Policy storedPolicy = optionalPolicy.get();
                    Policy policy = mapper.mapRequestToPolicy(policyRequest, policyRequest.getId());
                    policy.setCreatedBy(storedPolicy.getCreatedBy());
                    policy.setCreatedDate(storedPolicy.getCreatedDate());
                    policies.add(policy);
                }
        );
        return policyRepository.saveAll(policies);
    }
}
