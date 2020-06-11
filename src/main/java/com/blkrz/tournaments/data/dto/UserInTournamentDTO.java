package com.blkrz.tournaments.data.dto;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;

@Data
public class UserInTournamentDTO
{
    @NotNull(message = "Please provide your licence number.")
    @NotEmpty(message = "Please provide your licence number.")
    private String licence;
    @NotNull(message = "Please select your current position in ranking.")
    @Positive(message = "Ranking position must be positive")
    private Integer rank;
}
