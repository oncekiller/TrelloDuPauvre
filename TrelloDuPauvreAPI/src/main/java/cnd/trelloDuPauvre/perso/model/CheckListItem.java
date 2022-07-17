package cnd.trelloDuPauvre.perso.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Table(name = "checkListItem")
public class CheckListItem {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int checkListItemId;

    @Column(name = "label")
    private String label;

    @Column(name = "checked")
    @NotNull
    private Boolean checked;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticketId")
    @NotNull
    @JsonBackReference
    private Ticket ticket;

    public CheckListItem(String label, Boolean checked) {
        this.label = label;
        this.checked = checked;
    }

    public String toStringForTest() {
        return "{" +
                    "\"checkListItemId\":" + checkListItemId +
                    ", \"label\":\"" + label + "\"" +
                    ", \"checked\":" + checked +
                    ", \"ticket\": " +
                        "{ \"ticketId\" : " + ticket.getTicketId()  + "}" +
                '}';
    }
}
