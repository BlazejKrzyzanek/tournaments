package com.blkrz.tournaments.db.model;

import com.sun.istack.NotNull;
import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Data
@Entity
public class Sponsor
{
    @Id
    private Integer id;
    @NotNull
    private String name;
    private String logoPath;
    @ManyToMany
    private List<Tournament> tournaments;
}
