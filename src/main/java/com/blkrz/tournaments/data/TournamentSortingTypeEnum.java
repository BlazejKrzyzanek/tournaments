package com.blkrz.tournaments.data;

import java.util.Arrays;

public enum TournamentSortingTypeEnum
{
    NAME_ASC,
    DEADLINE_DESC;

    /** returns NAME_ASC if not found. */
    public static TournamentSortingTypeEnum getEnumByName(String sortOrder)
    {
        return Arrays.stream(TournamentSortingTypeEnum.values()).filter(o -> o.name().equals(sortOrder)).findAny().orElse(NAME_ASC);
    }
}
