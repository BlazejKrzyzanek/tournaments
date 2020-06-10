package com.blkrz.tournaments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class SponsorDoesntExistException extends Exception
{
    public SponsorDoesntExistException(String message)
    {
        super(message);
    }
}
