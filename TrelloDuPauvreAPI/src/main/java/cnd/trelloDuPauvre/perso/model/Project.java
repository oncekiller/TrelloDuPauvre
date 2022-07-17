package cnd.trelloDuPauvre.perso.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.Type;

import javax.validation.constraints.NotNull;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "project")
public class Project {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int projectId;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "isFavorite")
    @NotNull
    private Boolean isFavorite;

    @Column(name = "creationDate")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @Column(name = "lastConsultationDate")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastConsultationDate;

    @Column(name = "bgColor")
    private String bgColor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "imageId")
    private Image bgImage;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Story> stories = new HashSet<>();

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "project", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Ticket> tickets = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "workspaceId")
    private Workspace workspace;

    public Project(String name, LocalDateTime creationDate) {
        this.name = name;
        this.creationDate = creationDate;
    }

}