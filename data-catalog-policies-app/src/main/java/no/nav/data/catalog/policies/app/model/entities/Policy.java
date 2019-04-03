package no.nav.data.catalog.policies.app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name="POLICY")
@Data
@AllArgsConstructor
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_policy")
    @GenericGenerator(name = "seq_policy", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "SEQ_POLICY"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "200000000")})
    @Column(name="policy_id")
    private Long policyId;

    //TODO Relation ro InformationType
    @Column(name="information_type_id")
    private Long informationTypeId;

    @ManyToOne
    @JoinColumn(name="purpose_id")
    private Purpose purpose;

    @ManyToOne
    @JoinColumn(name="legal_basis_id")
    private LegalBasis legalBasis;

    /**
     * Default constructor.
     */
    public Policy() {
    }

}
