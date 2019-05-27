package no.nav.data.catalog.policies.app.policy.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import no.nav.data.catalog.policies.app.common.auditing.Auditable;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.*;

@Entity
@Table(name="LEGAL_BASIS")
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LegalBasis extends Auditable<String> {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_legalBasis")
    @GenericGenerator(name = "seq_legalBasis", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@Parameter(name = "sequence_name", value = "SEQ_LEGAL_BASIS")})
    @Column(name = "LEGAL_BASIS_ID", nullable = false, updatable = false, unique = true)
    private Long legalBasisId;

    @Column(name = "DESCRIPTION", length = 500)
    private String description;
}
