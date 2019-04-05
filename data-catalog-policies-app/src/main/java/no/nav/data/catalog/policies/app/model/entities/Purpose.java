package no.nav.data.catalog.policies.app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name="PURPOSE")
@Data
@AllArgsConstructor
public class Purpose {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_purpose")
    @GenericGenerator(name = "seq_purpose", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_PURPOSE")})
    @Column(name="purpose_id", nullable = false, updatable = false, unique = true)
    private Long purposeId;

    @Column(name="purpose_code", nullable = false, updatable = false, unique = true, length = 10)
    private String purposeCode;

    @Column(name="description", length = 500)
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
