package com.blkrz.tournaments.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.UNAUTHORIZED)
public class UserNotLoggedInException extends Exception
{
    public UserNotLoggedInException(String message)
    {
        super(message);
    }
}
