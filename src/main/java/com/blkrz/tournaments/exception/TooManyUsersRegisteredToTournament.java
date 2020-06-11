package com.blkrz.tournaments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class TooManyUsersRegisteredToTournament extends Exception
{
    public TooManyUsersRegisteredToTournament(String message)
    {
        super(message);
    }
}
