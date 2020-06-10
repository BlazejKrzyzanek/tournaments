package com.blkrz.tournaments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST)
public class TournamentsFileException extends Exception
{
    public TournamentsFileException(String message)
    {
        super(message);
    }

    public TournamentsFileException(String message, Throwable cause)
    {
        super(message, cause);
    }
}
