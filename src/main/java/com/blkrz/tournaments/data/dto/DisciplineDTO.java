package com.blkrz.tournaments.data.dto;

import com.blkrz.tournaments.db.model.Discipline;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class DisciplineDTO
{
    @NotNull
    @NotEmpty
    @Length(min = 3, max = 24)
    private String name;

    @NotNull
    @NotEmpty
    private String imagePath;

    public DisciplineDTO(Discipline discipline)
    {
        name = discipline.getName();
        imagePath = discipline.getImagePath();
    }
}
