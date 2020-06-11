package com.blkrz.tournaments.db.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Table(
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"tournament_id", "rank"}),
                @UniqueConstraint(columnNames = {"tournament_id", "user_id"}),
                @UniqueConstraint(columnNames = {"rank", "licenceCode"})
        }
)
@Entity
public class UserInTournament
{
    @Id
    @GeneratedValue
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
    private String licenceCode;
    @NotNull
    private Integer rank;
}
