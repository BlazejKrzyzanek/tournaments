package com.blkrz.tournaments.controller;

import com.blkrz.tournaments.data.TournamentSortingTypeEnum;
import com.blkrz.tournaments.data.dto.SimpleTournamentDTO;
import com.blkrz.tournaments.service.TournamentService;
import com.blkrz.tournaments.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.stream.Collectors;

@Controller
public class TournamentsController
{
    private final TournamentService tournamentService;
    private final UserService userService;

    @Autowired
    public TournamentsController(TournamentService tournamentService, UserService userService)
    {
        this.tournamentService = tournamentService;
        this.userService = userService;
    }

    @RequestMapping(value = {"/", "/tournaments"})
    public String getTournamentsRedirect1()
    {
        return "redirect:/tournaments/view";
    }

    @GetMapping("/tournaments/view")
    public String getTournaments(Model model,
                                      @RequestParam(name = "page", required = false) Integer page,
                                      @RequestParam(name = "pageEntries", required = false) Integer pageEntries,
                                      @RequestParam(name = "search", required = false) String search,
                                      @RequestParam(name = "sorting", required = false) String sortOrder)
    {
        Page<SimpleTournamentDTO> tournaments = tournamentService.getTournaments(page, pageEntries, search, TournamentSortingTypeEnum.getEnumByName(sortOrder));
        model.addAttribute("tournaments", tournaments.get().collect(Collectors.toList()));
        model.addAttribute("totalPages", tournaments.getTotalPages());
        model.addAttribute("disciplines", tournamentService.getAllDisciplines());
        model.addAttribute("isUserLogged", userService.isUserLogged());

        return "tournaments/tournamentsList";
    }
}
