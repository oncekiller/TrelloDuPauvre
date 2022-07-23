package cnd.trelloDuPauvre.perso.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "workspace")
public class Workspace {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int workspaceId;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "iconName")
    private String iconName;

    @Column(name = "creationDate")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "workspace", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Project> projects = new HashSet<>();

    public Workspace(String name, String iconName, LocalDateTime creationDate) {
        this.name = name;
        this.iconName = iconName;
        this.creationDate = creationDate;
    }
}
