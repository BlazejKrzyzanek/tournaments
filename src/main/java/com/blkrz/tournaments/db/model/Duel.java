package com.blkrz.tournaments.db.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;

@Data
@Entity
public class Duel
{
    @Id
    @GeneratedValue
    private Integer id;
    @ManyToOne
    private Round round;
    @NotNull
    private Integer number;
    @ManyToOne
    private User first;
    @ManyToOne
    private User second;
    @ManyToOne
    private User winner;
}
