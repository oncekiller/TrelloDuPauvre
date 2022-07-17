package cnd.trelloDuPauvre.perso.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "status")
public class Status {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int statusId;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "label")
    private String label;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "status")
    @JsonIgnore
    private Set<Ticket> tickets = new HashSet<>();

    public Status(String name, String label) {
        this.name = name;
        this.label = label;
    }

}
