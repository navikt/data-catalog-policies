package no.nav.data.catalog.policies.app.model;

import lombok.Data;
import javax.persistence.*;

@Entity
@Table(name="T_POLICY")
@Data
public class Policy {
    @Id
    @GeneratedValue(generator = "policy_generator")
    @SequenceGenerator(
            name = "policy_generator",
            sequenceName = "policy_generator",
            initialValue = 1000
    )
    @Column(name="id")
    private Long id;

    @Column(name="information_type_id")
    private String informationTypeId;

    @Column(name="policy_code")
    private String policyCode;

}
