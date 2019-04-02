package no.nav.data.catalog.policies.app.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="LEGAL_BASIS")
@Builder
@Data
@AllArgsConstructor
public class LegalBasis {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_legalBasis")
    @GenericGenerator(name = "seq_legalBasis", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_LEGAL_BASIS"),
                    @Parameter(name = "initial_value", value = "200000000")})
    @Column(name="legal_basis_id")
    private Long legalBasisId;

    @Column(name="description")
    private String description;

    @OneToMany(mappedBy = "legalBasis")
    private Set<Policy> policyList = new HashSet<>();

    /**
     * Default constructor.
     */
    public LegalBasis() {
    }
}
