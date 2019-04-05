package no.nav.data.catalog.policies.app.rest;

import no.nav.data.catalog.policies.app.model.common.PolicyRequest;
import no.nav.data.catalog.policies.app.model.entities.LegalBasis;
import no.nav.data.catalog.policies.app.model.entities.Policy;
import no.nav.data.catalog.policies.app.model.entities.Purpose;
import no.nav.data.catalog.policies.app.service.PolicyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@CrossOrigin
@RequestMapping("/policies")
public class PolicyRestController {

    @Autowired
    private PolicyService service;

    @GetMapping("/policy")
    public Page<Policy> getPolicies(Pageable pageable) {
        return service.getPolicies(pageable);
    }

    @PostMapping("/policy")
    public Policy createPolicy(@Valid @RequestBody PolicyRequest policyRequest) {
        return service.createPolicy(policyRequest);
    }

    @GetMapping("/policy/{id}")
    public Policy getPolicy(@PathVariable Long id) {
        return service.getPolicy(id);
    }

    @DeleteMapping("/policy/{id}")
    public void deletePolicy(@PathVariable Long id) {
        service.deletePolicy(id);
    }

    @PutMapping("/policy/{id}")
    public Policy updatePolicy(@PathVariable Long id, @Valid @RequestBody PolicyRequest policyRequest) {
        return service.updatePolicy(id, policyRequest);
    }

    @GetMapping("/purpose")
    public List<Purpose> getPurposes() {
        return service.getPurposes();
    }

    @GetMapping("/legalbasis")
    public List<LegalBasis> getLegalBasis() {
        return service.getLegalBasis();
    }
}
