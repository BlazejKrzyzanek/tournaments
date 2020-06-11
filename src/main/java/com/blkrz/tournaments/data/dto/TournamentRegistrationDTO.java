package com.blkrz.tournaments.data.dto;

import com.blkrz.tournaments.data.validator.IsInFuture;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Data
public class TournamentRegistrationDTO
{
    private Integer id;
    @NotNull(message = "Tournament name can't be empty.")
    @NotEmpty(message = "Tournament name can't be empty.")
    @Length(min = 3, max = 50, message = "Tournament name length must be between 3 and 50.")
    private String name;
    @NotNull(message = "Choose exactly one discipline.")
    private String discipline;
    @Length(max = 2048, message = "Description can't be longer than 2048.")
    private String description;
    @NotNull(message = "Please choose location.")
    private Double latitude;
    @NotNull(message = "Please choose location.")
    private Double longitude;
    @NotNull(message = "Entry limit must be positive.")
    @Positive(message = "Entry limit must be positive.")
    private Integer entryLimit;
    @DateTimeFormat(pattern = "dd-MM-yyyy HH:mm")
    @NotNull(message = "Choose deadline.")
    @IsInFuture(message = "Choose deadline in the future.")
    private String deadline;
    @NotNull(message = "Choose at least one sponsor.")
    private List<String> sponsors;

    private SponsorRegistrationDTO newSponsor;

    public LocalDateTime getDeadlineAsLocalDateTime()
    {
        return LocalDateTime.parse(deadline.replace('T', ' '), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    public void setDeadlineAsLocalDateTime(LocalDateTime localDateTime)
    {
        this.deadline = localDateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")).replace(' ', 'T');
    }
}
