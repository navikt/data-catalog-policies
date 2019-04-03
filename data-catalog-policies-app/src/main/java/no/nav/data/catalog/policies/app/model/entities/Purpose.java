package no.nav.data.catalog.policies.app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

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

//    @OneToMany(mappedBy = "purpose")
//    @JsonIgnore
//    private Set<Policy> policyList = new HashSet<>();

    /**
     * Default constructor.
     */
    public Purpose() {
    }
}
