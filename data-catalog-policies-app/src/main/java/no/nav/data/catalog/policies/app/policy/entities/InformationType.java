package no.nav.data.catalog.policies.app.policy.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.ReadOnlyProperty;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "INFORMATION_TYPE", schema = "BACKEND_SCHEMA")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InformationType {

    @Id
    @ReadOnlyProperty
    @Column(name = "information_type_id")
    private Long informationTypeId;

    @ReadOnlyProperty
    @Column(name = "name")
    private String name;

    @ReadOnlyProperty
    @Column(name = "description")
    private String description;
}
