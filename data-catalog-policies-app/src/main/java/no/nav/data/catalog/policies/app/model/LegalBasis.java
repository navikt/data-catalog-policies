package no.nav.data.catalog.policies.app.model;

import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name="LEGAL_BASIS")
@Data
public class LegalBasis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="legal_basis_id")
    private Long legalBasisId;

    @Column(name="description")
    private String description;

    @OneToMany(mappedBy = "legalBasis")
    private Set<Policy> policiyListe = new HashSet<>();
}
