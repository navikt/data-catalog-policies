package no.nav.data.catalog.policies.app.behandlingsgrunnlag;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import no.nav.data.catalog.policies.app.policy.domain.Dataset;
import no.nav.data.catalog.policies.app.policy.repository.PolicyRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static java.util.stream.Collectors.toList;

@Slf4j
@RestController
@CrossOrigin
@Api(value = "Data Catalog Policies", description = "REST API for Behandlingsgrunnlag", tags = {"Behandlingsgrunnlag"})
@RequestMapping("/behandlingsgrunnlag")
public class BehandlingsgrunnlagController {

    private final PolicyRepository policyRepository;

    public BehandlingsgrunnlagController(PolicyRepository policyRepository) {
        this.policyRepository = policyRepository;
    }


    @ApiOperation(value = "Get Behandlingsgrunnlag for Purpose", tags = {"Behandlingsgrunnlag"})
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "All Behandlingsgrunnlag fetched", response = BehandlingsgrunnlagResponse.class),
            @ApiResponse(code = 500, message = "Internal server error")})
    @GetMapping("/purpose/{purpose}")
    public ResponseEntity<BehandlingsgrunnlagResponse> getPolicies(@PathVariable String purpose) {
        var datasets = policyRepository.findDatasetsByPurposeCode(purpose).stream().map(Dataset::convertToResponse).collect(toList());
        return ResponseEntity.ok(new BehandlingsgrunnlagResponse(purpose, datasets));
    }
}
