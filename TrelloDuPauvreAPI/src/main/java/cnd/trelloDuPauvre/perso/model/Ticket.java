package cnd.trelloDuPauvre.perso.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int ticketId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "storyId")
    @NotNull
    private Story story;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "projectId")
    @NotNull
    private Project project;

    @Column(name = "name")
    @NotNull
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "priority")
    @NotNull
    private int priority;

    @Column(name = "deadLine")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadLine;

    @Column(name = "creationDate")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime creationDate;

    @Column(name = "modificationDate")
    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime modificationDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "natureId")
    @NotNull
    private Nature nature;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    @JoinColumn(name = "statusId")
    @NotNull
    private Status status;

    @OneToMany(fetch = FetchType.LAZY, mappedBy = "ticket", orphanRemoval = true, cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<CheckListItem> checkListItems = new HashSet<>();

    @Column(name = "frontIndex")
    @NotNull
    private int frontIndex;

    public Ticket(String name, String description, int priority, LocalDate deadLine, LocalDateTime creationDate, LocalDateTime modificationDate, int frontIndex) {
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.deadLine = deadLine;
        this.creationDate = creationDate;
        this.modificationDate = modificationDate;
        this.frontIndex = frontIndex;
    }

}
