package com.blkrz.tournaments.data.dto;

import com.blkrz.tournaments.data.validator.IsInFuture;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class TournamentRegistrationDTO
{

    @NotNull
    private String name;
    @NotNull
    private String discipline;
    @NotNull
    private String description;
    @NotNull
    private Boolean online;
    private Double latitude;
    private Double longitude;
    @NotNull
    private Integer entryLimit;
    @NotNull
    @IsInFuture
    private LocalDateTime deadline;
    @NotNull
    private List<String> sponsors;
}
