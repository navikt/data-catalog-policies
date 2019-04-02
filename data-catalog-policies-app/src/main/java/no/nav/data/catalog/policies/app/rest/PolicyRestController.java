package no.nav.data.catalog.policies.app.rest;

import no.nav.data.catalog.policies.app.model.Policy;
import no.nav.data.catalog.policies.app.repository.PolicyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/policies")
public class PolicyRestController {

    @Autowired
    private PolicyRepository repository;

    @GetMapping("/all")
    public Page<Policy> getPolicies(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @PostMapping("/policy")
    public Policy createPolicy(@Valid @RequestBody Policy policy) {
        return repository.save(policy);
    }

}
