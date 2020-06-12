package com.blkrz.tournaments.db.model;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
public class Round
{
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private Elimination elimination;
    @NotNull
    private Integer roundNumber;
    @OneToMany(mappedBy = "round", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Duel> duels;
}
