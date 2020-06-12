package com.blkrz.tournaments.data.dto;

import com.blkrz.tournaments.data.validator.PasswordMatches;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@PasswordMatches
public class UserRegistrationDTO implements DTOWithPassword
{
    @NotNull(message = "First name must not be empty.")
    @NotEmpty(message = "First name must not be empty.")
    private String firstName;

    @NotNull(message = "Last name must not be empty.")
    @NotEmpty(message = "Last name must not be empty.")
    private String lastName;

    @NotNull(message = "Password must not be empty.")
    @NotEmpty(message = "Password must not be empty.")
    @Length(min=6, max=20, message = "Password length must be between 6 and 20.")
    private String password;
    private String matchingPassword;

    @Email(message = "Plase provide valid email.")
    @NotNull(message = "Email must not be empty.")
    @NotEmpty(message = "Email must not be empty.")
    private String email;
}
