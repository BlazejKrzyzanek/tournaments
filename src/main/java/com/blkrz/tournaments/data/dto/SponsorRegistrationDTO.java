package com.blkrz.tournaments.data.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SponsorRegistrationDTO
{
    @NotNull
    @NotEmpty
    @Length(min = 3, max = 24)
    private String name;
    private MultipartFile logo;

    private String directoryPath;

}
