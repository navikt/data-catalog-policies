package no.nav.data.catalog.policies.app.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name="POLICY")
@Data
public class Policy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="policy_id")
    private Long policyId;

    //TODO Relation ro InformationType
    @Column(name="information_type_id")
    private Long informationTypeId;

    @ManyToOne
    @JoinColumn(name="purpose_id", nullable=false)
    private Purpose purpose;

    @ManyToOne
    @JoinColumn(name="legal_basis_id", nullable=false)
    private LegalBasis legalBasis;
}
