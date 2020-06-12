package com.blkrz.tournaments.db.model;

import lombok.Data;
import org.hibernate.annotations.NaturalId;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
public class Tournament
{
    @Id
    @GeneratedValue
    private Integer id;
    @NotNull
    @NaturalId
    @Length(min = 3, max = 50)
    private String name;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline discipline;
    @Length(max = 2048)
    private String description;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "organiser_id")
    private User organiser;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
    @NotNull
    private Integer entryLimit;
    @NotNull
    private LocalDateTime deadline;
    @ManyToMany
    private List<Sponsor> sponsors;
    @OneToOne
    private Elimination elimination;
}
