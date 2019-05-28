package no.nav.data.catalog.policies.app.policy.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.data.catalog.policies.app.common.auditing.Auditable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name="POLICY")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Policy extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_policy")
    @GenericGenerator(name = "seq_policy", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_POLICY")})
    @Column(name="POLICY_ID", nullable = false, updatable = false, unique = true)
    private Long policyId;

    @ManyToOne
    @JoinColumn(name="INFORMATION_TYPE_ID",  nullable = false)
    private InformationType informationType;

    @Column(name="PURPOSE_CODE",  nullable = false)
    private String purposeCode;

    @Column(name="LEGAL_BASIS_DESCRIPTION", length = 500)
    private String legalBasisDescription;
}
