package no.nav.data.catalog.policies.app.model.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;

@Entity
@Table(name="PURPOSE")
@Data
@AllArgsConstructor
public class Purpose {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_purpose")
    @GenericGenerator(name = "seq_purpose", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
            parameters = {@org.hibernate.annotations.Parameter(name = "sequence_name", value = "SEQ_PURPOSE"),
                    @org.hibernate.annotations.Parameter(name = "initial_value", value = "1")})
    @Column(name="purpose_id")
    private Long purposeId;

    @Column(name="purpose_code")
    private String purposeCode;

    @Column(name="description")
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
