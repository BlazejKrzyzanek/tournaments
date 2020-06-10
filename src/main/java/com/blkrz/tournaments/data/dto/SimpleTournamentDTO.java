package com.blkrz.tournaments.data.dto;

import com.blkrz.tournaments.db.model.Tournament;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SimpleTournamentDTO
{
    private String name;
    private String discipline;
    private Integer entryLimit;
    private LocalDateTime deadline;

    public SimpleTournamentDTO(Tournament tournament)
    {
        this(tournament.getName(),
                tournament.getDiscipline().getName(),
                tournament.getEntryLimit(),
                tournament.getDeadline());
    }

    public SimpleTournamentDTO(String name, String discipline, Integer entryLimit, LocalDateTime deadline)
    {
        this.name = name;
        this.discipline = discipline;
        this.entryLimit = entryLimit;
        this.deadline = deadline;
    }
}
