package com.blkrz.tournaments.data.validator;

import com.blkrz.tournaments.data.dto.DTOWithPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordMatchesValidator implements ConstraintValidator<PasswordMatches, Object>
{

    @Override
    public void initialize(PasswordMatches constraintAnnotation)
    {
    }

    @Override
    public boolean isValid(Object obj, ConstraintValidatorContext context)
    {
        if (obj instanceof DTOWithPassword)
        {
            DTOWithPassword user = (DTOWithPassword) obj;
            return user.getPassword().equals(user.getMatchingPassword());
        }

        return false;
    }
}