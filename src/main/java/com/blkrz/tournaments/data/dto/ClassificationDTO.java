package com.blkrz.tournaments.data.dto;

import com.blkrz.tournaments.db.model.UserInTournament;
import lombok.Data;

@Data
public class ClassificationDTO
{
    private int rank;
    private String firstName;
    private String lastName;

    public ClassificationDTO(UserInTournament userInTournament)
    {
        this.rank = userInTournament.getRank();
        this.firstName = userInTournament.getUser().getFirstName();
        this.lastName = userInTournament.getUser().getLastName();
    }
}
