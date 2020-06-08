package com.blkrz.tournaments.data.dto;

import com.blkrz.tournaments.data.validator.PasswordMatches;
import lombok.Data;

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
    private String password;
    private String matchingPassword;

    private String token;
}
