package com.blkrz.tournaments.data.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoundDTO
{
    private int number;
    private List<DuelDTO> duels;
}
