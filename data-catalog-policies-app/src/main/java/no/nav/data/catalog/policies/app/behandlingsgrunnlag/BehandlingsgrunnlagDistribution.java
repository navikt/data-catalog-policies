package no.nav.data.catalog.policies.app.behandlingsgrunnlag;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "BEHANDLINGSGRUNNLAG_DISTRIBUTION")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BehandlingsgrunnlagDistribution {

    @Id
    @Column(name = "PURPOSE", nullable = false, updatable = false, unique = true)
    private String purpose;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS")
    private DistributionStatus status;

    public BehandlingsgrunnlagDistribution markChanged() {
        status = DistributionStatus.CHANGED;
        return this;
    }

    public BehandlingsgrunnlagDistribution markDistributed() {
        status = DistributionStatus.DISTRIBUTED;
        return this;
    }

    public static BehandlingsgrunnlagDistribution newForPurpose(String purpose) {
        return new BehandlingsgrunnlagDistribution(purpose, DistributionStatus.CHANGED);
    }
}
