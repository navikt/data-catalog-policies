package no.nav.data.catalog.policies.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="PURPOSE")
@Data
@AllArgsConstructor
public class Purpose {

    @Id
    @Column(name="purpose_id")
    private String purposeId;

    @Column(name="description")
    private String description;

    @OneToMany(mappedBy = "purpose")
    private Set<Policy> policyList = new HashSet<>();

    /**
     * Default constructor.
     */
    public Purpose() {
    }
}
