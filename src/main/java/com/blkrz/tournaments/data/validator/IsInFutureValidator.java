package com.blkrz.tournaments.data.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class IsInFutureValidator implements ConstraintValidator<IsInFuture, Object>
{
    @Override
    public void initialize(IsInFuture constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context)
    {
        try
        {
            LocalDateTime dateTime = LocalDateTime.parse(obj.toString().replace('T', ' '), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            return dateTime.isAfter(LocalDateTime.now());
        }
        catch (Exception e)
        {
            return false;
        }
    }
}
