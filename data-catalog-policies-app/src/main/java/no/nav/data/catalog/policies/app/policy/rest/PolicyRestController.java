package no.nav.data.catalog.policies.app.policy.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import no.nav.data.catalog.policies.app.policy.PolicyRequest;
import no.nav.data.catalog.policies.app.policy.entities.LegalBasis;
import no.nav.data.catalog.policies.app.policy.entities.Policy;
import no.nav.data.catalog.policies.app.policy.entities.Purpose;
import no.nav.data.catalog.policies.app.policy.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@Api(value = "Data Catalog Policies", description = "REST API for Policies", tags = { "Policies" })
public class PolicyRestController {

    @Autowired
    private PolicyService service;

    @ApiOperation(value = "Get all Policies", tags = { "Policies" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All policies fetched", response = Policy.class, responseContainer = "List"),
                @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/policy")
    public Page<Policy> getPolicies(Pageable pageable) {
        return service.getPolicies(pageable);
    }

    @ApiOperation(value = "Create Policy", tags = { "Policies" })
    @ApiResponses(value = {
            @ApiResponse(code = 201, message = "Policy successfully created", response = Policy.class),
            @ApiResponse(code = 400, message = "Illegal arguments"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PostMapping("/policy")
    @ResponseStatus(HttpStatus.CREATED)
    public Policy createPolicy(@Valid @RequestBody PolicyRequest policyRequest) {
        Policy policy = service.createPolicy(policyRequest);
        return policy;
    }

    @ApiOperation(value = "Get Policy", tags = { "Policies" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All policies fetched", response = Policy.class),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/policy/{id}")
    public Policy getPolicy(@PathVariable Long id) {
        return service.getPolicy(id);
    }

    @ApiOperation(value = "Dekete Policy", tags = { "Policies" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Policy deleted"),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @DeleteMapping("/policy/{id}")
    public void deletePolicy(@PathVariable Long id) {
        service.deletePolicy(id);
    }

    @ApiOperation(value = "Update Policy", tags = { "Policies" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Policy updated"),
            @ApiResponse(code = 404, message = "Policy not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @PutMapping("/policy/{id}")
    public Policy updatePolicy(@PathVariable Long id, @Valid @RequestBody PolicyRequest policyRequest) {
        return service.updatePolicy(id, policyRequest);
    }

    @ApiOperation(value = "Get all Purposes", tags = { "Policies" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All purposes fetched", response = Purpose.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Purpose not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/purpose")
    public List<Purpose> getPurposes() {
        return service.getPurposes();
    }

    @ApiOperation(value = "Get all Legal bases", tags = { "Policies" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All Legal bases fetched", response = LegalBasis.class, responseContainer = "List"),
            @ApiResponse(code = 404, message = "Legal basis not found"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/legalbasis")
    public List<LegalBasis> getLegalBasis() {
        return service.getLegalBases();
    }
}