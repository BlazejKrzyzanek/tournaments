package com.blkrz.tournaments.controller;

import com.blkrz.tournaments.data.TournamentSortingTypeEnum;
import com.blkrz.tournaments.data.dto.SimpleTournamentDTO;
import com.blkrz.tournaments.data.dto.TournamentRegistrationDTO;
import com.blkrz.tournaments.data.dto.TournamentViewDTO;
import com.blkrz.tournaments.data.dto.UserInTournamentDTO;
import com.blkrz.tournaments.db.model.Tournament;
import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.db.model.UserInTournament;
import com.blkrz.tournaments.exception.*;
import com.blkrz.tournaments.service.TournamentService;
import com.blkrz.tournaments.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    public String showTournaments(Model model,
                                  @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                  @RequestParam(name = "pageEntries", required = false, defaultValue = "10") Integer pageEntries,
                                  @RequestParam(name = "search", required = false) String search,
                                  @RequestParam(name = "discipline", required = false) String discipline,
                                  @RequestParam(name = "sorting", required = false) String sortOrder)
    {
        Page<SimpleTournamentDTO> tournaments = tournamentService.getTournaments(page, pageEntries, search, discipline, TournamentSortingTypeEnum.getEnumByName(sortOrder));
        return showTournaments(model, page, tournaments);
    }

    private String showTournaments(Model model, @RequestParam(name = "page", required = false, defaultValue = "0") Integer page, Page<SimpleTournamentDTO> tournaments)
    {
        model.addAttribute("tournaments", tournaments.get().collect(Collectors.toList()));

        int totalPages = tournaments.getTotalPages();
        model.addAttribute("totalPages", totalPages);

        model.addAttribute("page", page);
        model.addAttribute("pageNumbers", IntStream.rangeClosed(Math.max(0, page - 3), Math.min(page + 3, totalPages - 1)).boxed().collect(Collectors.toList()));

        model.addAttribute("disciplines", tournamentService.getAllDisciplines());
        model.addAttribute("isUserLogged", userService.isUserLogged());

        return "tournaments/tournamentsList";
    }

    @GetMapping("/tournaments/my")
    public String showMyTournaments(Model model,
                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                    @RequestParam(name = "pageEntries", required = false, defaultValue = "10") Integer pageEntries,
                                    @RequestParam(name = "search", required = false) String search,
                                    @RequestParam(name = "discipline", required = false) String discipline,
                                    @RequestParam(name = "sorting", required = false) String sortOrder) throws UserNotLoggedInException
    {
        Page<SimpleTournamentDTO> tournaments = tournamentService.getTournamentsUserTakePart(userService.getLoggedInUser(), page, pageEntries, search, discipline);
        return showTournaments(model, page, tournaments);
    }

    @GetMapping("/tournaments/organised-by-me")
    public String showMyOrganisedTournaments(Model model,
                                    @RequestParam(name = "page", required = false, defaultValue = "0") Integer page,
                                    @RequestParam(name = "pageEntries", required = false, defaultValue = "10") Integer pageEntries,
                                    @RequestParam(name = "search", required = false) String search,
                                    @RequestParam(name = "discipline", required = false) String discipline,
                                    @RequestParam(name = "sorting", required = false) String sortOrder) throws UserNotLoggedInException
    {
        Page<SimpleTournamentDTO> tournaments = tournamentService.getTournamentsByOrganiser(userService.getLoggedInUser(), page, pageEntries, search, discipline);
        return showTournaments(model, page, tournaments);
    }

    @GetMapping("/tournaments/organise")
    public String showOrganiseForm(Model model)
    {
        if (!model.containsAttribute("tournamentRegistration"))
        {
            model.addAttribute("tournamentRegistration", new TournamentRegistrationDTO());
        }

        model.addAttribute("isUserLogged", userService.isUserLogged());
        model.addAttribute("disciplines", tournamentService.getAllDisciplines());
        model.addAttribute("sponsors", tournamentService.getAllSponsors());

        return "tournaments/tournamentsOrganise";
    }

    @PostMapping("/tournaments/organise")
    public String registerTournament(@ModelAttribute("tournamentRegistration") @Valid TournamentRegistrationDTO registrationDTO,
                                     BindingResult bindingResult,
                                     RedirectAttributes redirectAttributes,
                                     HttpServletRequest request) throws UserNotLoggedInException, SponsorDoesntExistException, DisciplineDoesntExistException, TournamentsFileException
    {
        if (!bindingResult.hasErrors())
        {
            registrationDTO.getNewSponsor().setDirectoryPath(request.getServletContext().getRealPath("/images/sponsors/"));
            tournamentService.registerTournament(registrationDTO, userService.getLoggedInUser());
            return "redirect:/tournaments/view";
        }

        redirectAttributes.addFlashAttribute("tournamentRegistration", registrationDTO);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tournamentRegistration", bindingResult);
        return "redirect:/tournaments/organise";
    }

    @GetMapping("/tournaments/view/{tournamentName}")
    public String showTournamentDetails(@PathVariable String tournamentName, Model model)
    {
        TournamentViewDTO viewDTO = tournamentService.getTournamentViewByName(tournamentName);

        if (model.containsAttribute("userInTournament"))
        {
            viewDTO.setIsUserRegistering(true);
        }
        else
        {
            model.addAttribute("userInTournament", new UserInTournamentDTO());
            viewDTO.setIsUserRegistering(false);
        }

        if (model.containsAttribute("registrationError"))
        {
            viewDTO.setIsUserRegistering(true);
        }

        try
        {
            User user = userService.getLoggedInUser();
            viewDTO.setIsUserOrganiser(viewDTO.getOrganiserEmail().equals(user.getEmail()));
            viewDTO.setIsUserRegistered(tournamentService.isUserRegisteredOnTournament(user, tournamentName));
            model.addAttribute("isUserLogged", true);
        }
        catch (UserNotLoggedInException e)
        {
            viewDTO.setIsUserOrganiser(false);
            viewDTO.setIsUserRegistered(false);
            model.addAttribute("isUserLogged", false);
        }

        model.addAttribute("view", viewDTO);

        return "tournaments/tournamentsView";
    }

    @PostMapping("/tournaments/register/{tournamentName}")
    public String registerUserToTournament(@PathVariable String tournamentName,
                                           @ModelAttribute("userInTournament") @Valid UserInTournamentDTO userInTournamentDTO,
                                           BindingResult bindingResult,
                                           RedirectAttributes redirectAttributes) throws UserNotLoggedInException, TooManyUsersRegisteredToTournament
    {
        if (!bindingResult.hasErrors())
        {
            UserInTournament inTournament = tournamentService.registerUserToTournament(userService.getLoggedInUser(), tournamentName, userInTournamentDTO.getLicence(), userInTournamentDTO.getRank());
            if (inTournament == null)
            {
                redirectAttributes.addFlashAttribute("registrationError", "Licence and rank must be uniq per tournament.");
            }
        }
        else
        {
            redirectAttributes.addFlashAttribute("userInTournament", userInTournamentDTO);
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userInTournament", bindingResult);
        }

        return "redirect:/tournaments/view/" + tournamentName;
    }

    @GetMapping("/tournaments/edit/{tournamentName}")
    public String showEditTournamentDetails(@PathVariable String tournamentName, Model model)
    {
        if (!model.containsAttribute("tournamentRegistration"))
        {
            model.addAttribute("tournamentRegistration", tournamentService.getTournamentAsRegistrationDTO(tournamentName));
        }

        model.addAttribute("isUserLogged", userService.isUserLogged());
        model.addAttribute("disciplines", tournamentService.getAllDisciplines());
        model.addAttribute("sponsors", tournamentService.getAllSponsors());

        return "tournaments/tournamentsEdit";
    }

    @PostMapping("/tournaments/edit/{tournamentName}")
    public String editTournamentDetails(@PathVariable String tournamentName,
                                        @ModelAttribute("tournamentRegistration") @Valid TournamentRegistrationDTO registrationDTO,
                                        BindingResult bindingResult,
                                        RedirectAttributes redirectAttributes) throws SponsorDoesntExistException
    {
        if (!bindingResult.hasErrors())
        {
            Tournament tournament = tournamentService.updateTournament(registrationDTO);
            if (tournament == null)
            {
                redirectAttributes.addFlashAttribute("error", "Such tournament already exist, choose different name.");
                redirectAttributes.addFlashAttribute("tournamentRegistration", registrationDTO);
                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tournamentRegistration", bindingResult);
            }
            return "redirect:/tournaments/view";
        }

        redirectAttributes.addFlashAttribute("tournamentRegistration", registrationDTO);
        redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.tournamentRegistration", bindingResult);
        return "redirect:/tournaments/organise";
    }
}
