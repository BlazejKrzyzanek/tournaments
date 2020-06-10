package com.blkrz.tournaments.db.model;

import lombok.Data;
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
    @Length(min = 3, max = 50)
    private String name;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "discipline_id")
    private Discipline discipline;
    @NotNull
    @Length(max = 2048)
    private String description;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "organiser_id")
    private User organiser;
    private Double latitude;
    private Double longitude;
    @NotNull
    private Boolean online;
    @NotNull
    private Integer entryLimit;
    @NotNull
    private LocalDateTime deadline;
    @ManyToMany
    private List<Sponsor> sponsors;
}
