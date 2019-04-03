package no.nav.data.catalog.policies.app.rest;

import no.nav.data.catalog.policies.app.model.common.PolicyRequest;
import no.nav.data.catalog.policies.app.model.entities.LegalBasis;
import no.nav.data.catalog.policies.app.model.entities.Policy;
import no.nav.data.catalog.policies.app.model.entities.Purpose;
import no.nav.data.catalog.policies.app.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/policies")
public class PolicyRestController {

    @Autowired
    private PolicyService service;

    @GetMapping("/allpolicies")
    public List<Policy> getPolicies() {
        return service.getPolicies();
    }

    @GetMapping("/allpurposes")
    public List<Purpose> getPurposes() {
        return service.getPurposes();
    }

    @GetMapping("/allegalbasis")
    public List<LegalBasis> getLegalBasis() {
        return service.getLegalBasis();
    }

    @PostMapping("/policy")
    public Policy createPolicy(@Valid @RequestBody PolicyRequest policyRequest) {
        Policy policy = service.createPolicy(policyRequest);
        return policy;
    }
}
