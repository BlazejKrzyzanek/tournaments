package com.blkrz.tournaments.controller;

import com.blkrz.tournaments.db.model.Discipline;
import com.blkrz.tournaments.db.model.Sponsor;
import com.blkrz.tournaments.exception.DisciplineDoesntExistException;
import com.blkrz.tournaments.exception.SponsorDoesntExistException;
import com.blkrz.tournaments.exception.TournamentsFileException;
import com.blkrz.tournaments.service.FileService;
import com.blkrz.tournaments.service.TournamentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ImagesController
{
    private final TournamentService tournamentService;
    private final FileService fileService;

    @Autowired
    public ImagesController(TournamentService tournamentService, FileService fileService)
    {
        this.tournamentService = tournamentService;
        this.fileService = fileService;
    }

    @RequestMapping(value = "/images/disciplines/{disciplineName}")
    @ResponseBody
    public byte[] getDisciplineImage(@PathVariable String disciplineName, HttpServletRequest request) throws DisciplineDoesntExistException, TournamentsFileException
    {
        Discipline discipline = tournamentService.getDisciplineByName(disciplineName);

        return fileService.getSavedImage(discipline.getImagePath());
    }

    @RequestMapping(value = "/images/sponsors/{sponsorName}")
    @ResponseBody
    public byte[] getSponsorLogo(@PathVariable String sponsorName, HttpServletRequest request) throws SponsorDoesntExistException, TournamentsFileException
    {
        Sponsor sponsor = tournamentService.getSponsorByName(sponsorName);

        return fileService.getSavedImage(sponsor.getLogoPath());
    }
}
