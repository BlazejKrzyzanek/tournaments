package com.blkrz.tournaments.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInDuelDTO
{
    private String displayName;
    private boolean isWinner;
    private Integer userId;

}
