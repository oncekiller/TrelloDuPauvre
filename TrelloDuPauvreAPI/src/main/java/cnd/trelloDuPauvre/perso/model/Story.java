package cnd.trelloDuPauvre.perso.model;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

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
@Table(name = "story")
public class Story {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int storyId;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "creationDate")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @Column(name = "frontIndex")
    @NotNull
    private int frontIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "projectId")
    @NotNull
    private Project project;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "story", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Ticket> tickets = new HashSet<>();

    public Story(String name, LocalDateTime creationDate) {
        this.name = name;
        this.creationDate = creationDate;
    }
}
