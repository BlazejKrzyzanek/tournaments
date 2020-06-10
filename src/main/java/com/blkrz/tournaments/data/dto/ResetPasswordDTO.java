package com.blkrz.tournaments.data.dto;

import com.blkrz.tournaments.data.validator.PasswordMatches;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@PasswordMatches
public class ResetPasswordDTO implements DTOWithPassword
{
    @Email
    @NotNull
    @NotEmpty
    private String email;
    @NotNull
    @NotEmpty
    @Length(min=6, max=20)
    private String password;
    private String matchingPassword;

    private String token;
}
