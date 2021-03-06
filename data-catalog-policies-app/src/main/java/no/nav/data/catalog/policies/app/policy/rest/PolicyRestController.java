package no.nav.data.catalog.policies.app.policy.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.behandlingsgrunnlag.BehandlingsgrunnlagService;
import no.nav.data.catalog.policies.app.common.exceptions.DataCatalogPoliciesNotFoundException;
import no.nav.data.catalog.policies.app.common.exceptions.ValidationException;
import no.nav.data.catalog.policies.app.common.web.PageParameters;
import no.nav.data.catalog.policies.app.common.web.RestResponsePage;
import no.nav.data.catalog.policies.app.dataset.DatasetConsumer;
import no.nav.data.catalog.policies.app.policy.PolicyService;
import no.nav.data.catalog.policies.app.policy.domain.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.domain.PolicyResponse;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.mapper.PolicyMapper;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cache.CacheManager;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Valid;

import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.toList;
import static no.nav.data.catalog.policies.app.common.cache.CacheConfig.CODELIST_CACHE;
import static no.nav.data.catalog.policies.app.common.cache.CacheConfig.DATASET_BY_ID_CACHE;
import static no.nav.data.catalog.policies.app.common.cache.CacheConfig.DATASET_BY_TITLE_CACHE;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "Data Catalog Policies", description = "REST API for Policies", tags = {"Policies"})
@RequestMapping("/policy")
public class PolicyRestController {

    private final PolicyService service;
    private final PolicyMapper mapper;
    private final PolicyRepository policyRepository;
    private final CacheManager cachemanager;
    private final DatasetConsumer datasetConsumer;
    private final BehandlingsgrunnlagService behandlingsgrunnlagService;

    public PolicyRestController(PolicyService service, PolicyMapper mapper, PolicyRepository policyRepository, CacheManager cachemanager,
            DatasetConsumer datasetConsumer, BehandlingsgrunnlagService behandlingsgrunnlagService) {
        this.service = service;
        this.mapper = mapper;
        this.policyRepository = policyRepository;
        this.cachemanager = cachemanager;
        this.datasetConsumer = datasetConsumer;
        this.behandlingsgrunnlagService = behandlingsgrunnlagService;
    }

    @ApiOperation(value = "Get all Policies, get for datasetId will always return all policies", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All policies fetched", response = PolicyPage.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping
    public ResponseEntity<RestResponsePage<PolicyResponse>> getPolicies(PageParameters pageParameters,
            @RequestParam(required = false) String datasetId,
            @ApiParam("If fetching for a dataset, include inactive policies. For all policies, inactive will always be included")
            @RequestParam(required = false, defaultValue = "false") Boolean includeInactive) {
        if (datasetId != null) {
            log.debug("Received request for Policies related to Dataset with id={}", datasetId);
            var policies = policyRepository.findByDatasetId(datasetId).stream()
                    .filter(policy -> includeInactive || policy.isActive())
                    .map(mapper::mapPolicyToResponse)
                    .collect(toList());
            return ResponseEntity.ok(new RestResponsePage<>(policies));
        } else {
            log.debug("Received request for all Policies");
            Page<PolicyResponse> policyResponses = policyRepository.findAll(pageParameters.createIdSortedPage()).map(mapper::mapPolicyToResponse);
            return ResponseEntity.ok(new RestResponsePage<>(policyResponses.getContent(), policyResponses.getPageable(), policyResponses.getTotalElements()));
        }
    }

    @ApiOperation(value = "Count all Policies", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Count policies fetched", response = Long.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/count")
    public ResponseEntity<Long> countPolicies() {
        log.debug("Received request for number of Policies");
        return ResponseEntity.ok(policyRepository.count());
    }

    @ApiOperation(value = "Count Policies by Dataset", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Count fetched", response = Long.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping(path = "/count", params = {"datasetId"})
    public ResponseEntity<Long> countPoliciesByDataset(@RequestParam String datasetId) {
        log.debug("Received request for number of policies related to Datasets with id={}", datasetId);
        return ResponseEntity.ok(policyRepository.countByDatasetId(datasetId));
    }

    @ApiOperation(value = "Create Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Policy successfully created", response = PolicyResponse.class, responseContainer = "List"),
            @ApiResponse(code = 400, message = "Illegal arguments"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<List<PolicyResponse>> createPolicy(@Valid @RequestBody List<PolicyRequest> policyRequests) {
        log.debug("Received request to create Policies");
        service.validateRequests(policyRequests, false);
        List<Policy> policies = policyRequests.stream().map(policy -> mapper.mapRequestToPolicy(policy, null)).collect(toList());
        onChange(policies);
        return new ResponseEntity<>(policyRepository.saveAll(policies).stream().map(mapper::mapPolicyToResponse).collect(Collectors.toList()), HttpStatus.CREATED);
    }

    @ApiOperation(value = "Get Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Fetched policy", response = PolicyResponse.class),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/{id}")
    public ResponseEntity<PolicyResponse> getPolicy(@PathVariable Long id) {
        log.debug("Received request for Policy with id={}", id);
        Optional<Policy> optionalPolicy = policyRepository.findById(id);
        if (optionalPolicy.isEmpty()) {
            throw notFoundError(id);
        }
        return ResponseEntity.ok(mapper.mapPolicyToResponse(optionalPolicy.get()));
    }

    @ApiOperation(value = "Delete Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Policy deleted"),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @DeleteMapping("/{id}")
    public void deletePolicy(@PathVariable Long id) {
        log.debug("Received request to delete Policy with id={}", id);
        Optional<Policy> optionalPolicy = policyRepository.findById(id);
        if (optionalPolicy.isEmpty()) {
            throw notFoundError(id);
        }
        onChange(List.of(optionalPolicy.get()));
        policyRepository.deleteById(id);
    }

    @ApiOperation(value = "Delete Policies by datasetId", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Policies deleted"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @DeleteMapping(params = {"datasetId"})
    public void deletePoliciesByDataset(@RequestParam String datasetId) {
        log.debug("Received request to delete Policies with datasetId={}", datasetId);
        if (StringUtils.isBlank(datasetId)) {
            throw new ValidationException("Blank datasetId");
        }
        long deletes = policyRepository.deleteByDatasetId(datasetId);
        datasetConsumer.syncDatasetById(List.of(datasetId));
        log.debug("Deleted {} policies", deletes);
    }

    @ApiOperation(value = "Update Policy", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Policy updated", response = PolicyResponse.class),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PutMapping("/{id}")
    public ResponseEntity<PolicyResponse> updatePolicy(@PathVariable Long id, @Valid @RequestBody PolicyRequest policyRequest) {
        log.debug("Received request to update Policy with id={}", id);
        if (policyRepository.findById(id).isEmpty()) {
            throw notFoundError(id);
        }
        if (!Objects.equals(id, policyRequest.getId())) {
            throw new ValidationException(String.format("id mismatch in request %d and path %d", policyRequest.getId(), id));
        }
        service.validateRequests(List.of(policyRequest), true);
        Policy policy = mapper.mapRequestToPolicy(policyRequest, id);
        onChange(List.of(policy));
        return ResponseEntity.ok(mapper.mapPolicyToResponse(policyRepository.save(policy)));
    }

    @ApiOperation(value = "Update Policies", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Polices updated", response = PolicyResponse.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PutMapping
    public ResponseEntity<List<PolicyResponse>> updatePolicies(@Valid @RequestBody List<PolicyRequest> policyRequests) {
        log.debug("Received requests to update Policies");
        service.validateRequests(policyRequests, true);
        List<Policy> policies = policyRequests.stream().map(policyRequest -> mapper.mapRequestToPolicy(policyRequest, policyRequest.getId())).collect(toList());
        onChange(policies);
        return ResponseEntity.ok(policyRepository.saveAll(policies).stream().map(mapper::mapPolicyToResponse).collect(Collectors.toList()));
    }

    @ApiOperation(value = "Cache evict", tags = {"Policies"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Cache evict", response = String.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PostMapping("/clearcache")
    public ResponseEntity clearCache() {
        requireNonNull(cachemanager.getCache(CODELIST_CACHE)).clear();
        requireNonNull(cachemanager.getCache(DATASET_BY_TITLE_CACHE)).clear();
        requireNonNull(cachemanager.getCache(DATASET_BY_ID_CACHE)).clear();
        return ResponseEntity.ok("OK");
    }

    private RuntimeException notFoundError(Long id) {
        String message = String.format("Cannot find Policy with id: %s", id);
        log.warn(message);
        throw new DataCatalogPoliciesNotFoundException(message);
    }

    private static final class PolicyPage extends RestResponsePage<PolicyResponse> {

    }

    private void onChange(List<Policy> policies) {
        policies.stream().map(Policy::getPurposeCode).distinct().forEach(behandlingsgrunnlagService::scheduleDistributeForPurpose);
        datasetConsumer.syncDatasetById(policies.stream().map(Policy::getDatasetId).collect(toList()));
    }
}
