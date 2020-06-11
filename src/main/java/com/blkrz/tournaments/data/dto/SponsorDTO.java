package com.blkrz.tournaments.data.dto;

import com.blkrz.tournaments.db.model.Sponsor;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class SponsorDTO
{
    @NotNull
    @NotEmpty
    @Length(min = 3, max = 24)
    private String name;

    private String logoPath;

    public SponsorDTO(Sponsor sponsor)
    {
        name = sponsor.getName();
        logoPath = sponsor.getLogoPath();
    }
}
