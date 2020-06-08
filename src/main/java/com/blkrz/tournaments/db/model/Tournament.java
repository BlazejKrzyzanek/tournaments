package com.blkrz.tournaments.db.model;

import lombok.Data;

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
    private String name;
    @NotNull
    private String discipline;
    private String description;
    @NotNull
    @ManyToOne
    @JoinColumn(name = "organiser_id")
    private User organiser;
    private Double latitude;
    private Double longitude;
    @NotNull
    private Integer entryLimit;
    @NotNull
    private LocalDateTime deadline;
    @ManyToMany
    private List<Sponsor> sponsors;
}
