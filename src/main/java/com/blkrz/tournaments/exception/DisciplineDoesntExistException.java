package com.blkrz.tournaments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class DisciplineDoesntExistException extends Exception
{
    public DisciplineDoesntExistException(String message)
    {
        super(message);
    }
}
