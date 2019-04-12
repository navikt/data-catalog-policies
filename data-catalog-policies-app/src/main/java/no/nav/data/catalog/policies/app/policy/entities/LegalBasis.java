package no.nav.data.catalog.policies.app.policy.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name="LEGAL_BASIS")
@Builder
@Data
@AllArgsConstructor
public class LegalBasis {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_legalBasis")
    @GenericGenerator(name = "seq_legalBasis", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_LEGAL_BASIS")})
    @Column(name="legal_basis_id",  nullable = false, updatable = false, unique = true)
    private Long legalBasisId;

    @Column(name="description", length = 500)
    private String description;

//    @OneToMany(mappedBy = "legalBasis")
//    private Set<Policy> policyList = new HashSet<>();

    /**
     * Default constructor.
     */
    public LegalBasis() {
    }
}
