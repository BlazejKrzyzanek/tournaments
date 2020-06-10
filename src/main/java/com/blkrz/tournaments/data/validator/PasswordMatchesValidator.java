package com.blkrz.tournaments.data.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object>
{
    @Override
    public void initialize(PasswordMatches constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context)
    {
        if (obj instanceof LocalDateTime)
        {
            LocalDateTime localDateTime = (LocalDateTime) obj;
            return localDateTime.isAfter(LocalDateTime.now());
        }

        return false;
    }
}