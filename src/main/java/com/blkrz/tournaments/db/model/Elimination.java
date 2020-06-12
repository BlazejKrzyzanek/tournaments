package com.blkrz.tournaments.db.model;

import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
public class Elimination
{
    @Id
    @GeneratedValue
    private Integer id;
    @NotNull
    @NaturalId
    @OneToOne
    private Tournament tournament;
    @OneToMany(mappedBy = "elimination", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Round> rounds;
}
