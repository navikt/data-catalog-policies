package no.nav.data.catalog.policies.app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name="POLICY")
@Data
@AllArgsConstructor
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_policy")
    @GenericGenerator(name = "seq_policy", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_POLICY")})
    @Column(name="policy_id", nullable = false, updatable = false, unique = true)
    private Long policyId;

    //TODO Relation to InformationType
    @Column(name="information_type_id",  nullable = false)
    private Long informationTypeId;

    @ManyToOne
    @JoinColumn(name="purpose_id",  nullable = false)
    private Purpose purpose;

    @ManyToOne
    @JoinColumn(name="legal_basis_id",  nullable = false)
    private LegalBasis legalBasis;

    /**
     * Default constructor.
     */
    public Policy() {
    }

}
