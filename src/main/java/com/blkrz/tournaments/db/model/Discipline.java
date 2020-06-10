package com.blkrz.tournaments.db.model;

import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
public class Discipline
{
    @Id
    @GeneratedValue
    private Integer id;

    @NotNull
    @NotEmpty
    @NaturalId
    private String name;

    private String imagePath;

    @OneToMany
    private List<Tournament> tournaments;

    public Discipline()
    {
    }

    public Discipline(String name)
    {
        this.name = name;
    }
}
