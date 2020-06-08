package com.blkrz.tournaments.db.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"tournament_id", "rank"}),
                @UniqueConstraint(columnNames = {"tournament_id", "user_id"})
        }
)
@Entity
public class UserInTournament
{
    @Id
    private Integer id;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "tournament_id")
    private Tournament tournament;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @NotNull
    @Column(unique = true)
    private String licenceCode;
    @NotNull
    private Integer rank;
}
