package no.nav.data.catalog.policies.app.policy.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import no.nav.data.catalog.policies.app.common.auditing.Auditable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name="PURPOSE")
@Data
@AllArgsConstructor
public class Purpose extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_purpose")
    @GenericGenerator(name = "seq_purpose", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_PURPOSE")})
    @Column(name="PURPOSE_ID", nullable = false, updatable = false, unique = true)
    private Long purposeId;

    @Column(name="PURPOSE_CODE", nullable = false, updatable = false, unique = true, length = 10)
    private String purposeCode;

    @Column(name="DESCRIPTION", length = 500)
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
