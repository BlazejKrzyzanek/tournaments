package com.blkrz.tournaments.db.model;

import com.sun.istack.NotNull;
import lombok.Data;
import org.hibernate.annotations.NaturalId;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import java.util.List;

@Data
@Entity
public class Sponsor
{
    @Id
    @GeneratedValue
    private Integer id;
    @NaturalId
    @NotNull
    private String name;
    
    private String logoPath;
    @ManyToMany
    private List<Tournament> tournaments;
}
