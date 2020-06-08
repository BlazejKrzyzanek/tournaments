package com.blkrz.tournaments.data.dto;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ForgotPasswordEmailDTO
{
    @Email
    @NotNull
    @NotEmpty
    private String email;
}
