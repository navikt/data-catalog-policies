package no.nav.data.catalog.policies.app.model;

import lombok.Data;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table(name="PURPOSE")
@Data
public class Purpose {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="purpose_id")
    private Long purposeId;

    @Column(name="description")
    private String description;

    @OneToMany(mappedBy = "purpose")
    private Set<Purpose> purposes;

}
