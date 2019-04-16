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
@Table(name = "BACKEND_SCHEMA.INFORMATION_TYPE")
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
    @Column(name = "information_type_name")
    private String informationTypeName;

    @ReadOnlyProperty
    @Column(name = "description")
    private String description;
}
