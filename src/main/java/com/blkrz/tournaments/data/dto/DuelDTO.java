package com.blkrz.tournaments.data.dto;

import com.blkrz.tournaments.db.model.Duel;
import com.blkrz.tournaments.db.model.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DuelDTO
{
    private UserInDuelDTO first;
    private UserInDuelDTO second;

    public DuelDTO(Duel duel)
    {
        if (duel != null)
        {
            User first = duel.getFirst();
            User second = duel.getSecond();
            User winner = duel.getWinner();
            this.setFirst(first == null ? null :
                    new UserInDuelDTO(first.getFirstName() + " " + first.getLastName(), winner != null && winner.equals(first), first.getId()));
            this.setSecond(second == null ? null :
                    new UserInDuelDTO(second.getFirstName() + " " + second.getLastName(), winner != null && winner.equals(second), second.getId()));
        }
    }
}
