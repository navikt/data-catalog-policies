package no.nav.data.catalog.policies.app.rest;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import no.nav.data.catalog.policies.app.model.common.PolicyRequest;
import no.nav.data.catalog.policies.app.model.entities.LegalBasis;
import no.nav.data.catalog.policies.app.model.entities.Policy;
import no.nav.data.catalog.policies.app.model.entities.Purpose;
import no.nav.data.catalog.policies.app.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@Api(value = "Data Catalog Policies", description = "REST API for Policies", tags = { "Policies" })
@RequestMapping("/policies")
public class PolicyRestController {

    @Autowired
    private PolicyService service;

    @ApiOperation(value = "Get all Policies", tags = { "Policies" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All policies fetched", response = Policy.class, responseContainer = "List"),
                @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/allpolicies")
    public List<Policy> getPolicies() {
        return service.getPolicies();
    }

    @ApiOperation(value = "Get all Purposes", tags = { "Policies" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All purposes fetched", response = Purpose.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/allpurposes")
    public List<Purpose> getPurposes() {
        return service.getPurposes();
    }

    @ApiOperation(value = "Get all Legal bases", tags = { "Policies" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All Legal bases fetched", response = LegalBasis.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/allegalbases")
    public List<LegalBasis> getLegalBases() {
        return service.getLegalBases();
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
}
