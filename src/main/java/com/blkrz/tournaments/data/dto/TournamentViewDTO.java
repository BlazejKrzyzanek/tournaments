package com.blkrz.tournaments.data.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentViewDTO
{
    private String name;
    private String organiserEmail;
    private DisciplineDTO discipline;
    private String description;
    private Double latitude;
    private Double longitude;
    private Integer entryLimit;
    private Integer entriesCount;
    private LocalDateTime deadline;
    private List<SponsorDTO> sponsors;
    private List<ClassificationDTO> classification;

    private Boolean isRegistrationOpen;
    private Boolean isUserRegistered;
    private Boolean isUserOrganiser;
    private Boolean isUserRegistering;
}
