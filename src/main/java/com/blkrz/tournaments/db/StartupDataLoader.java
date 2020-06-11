package com.blkrz.tournaments.db;

import com.blkrz.tournaments.data.dto.DisciplineDTO;
import com.blkrz.tournaments.data.dto.SponsorDTO;
import com.blkrz.tournaments.data.dto.TournamentRegistrationDTO;
import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.exception.DisciplineDoesntExistException;
import com.blkrz.tournaments.exception.SponsorDoesntExistException;
import com.blkrz.tournaments.exception.TournamentsFileException;
import com.blkrz.tournaments.service.TournamentService;
import com.blkrz.tournaments.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class StartupDataLoader implements ApplicationRunner
{

    private final UserService userService;
    private final TournamentService tournamentService;

    @Autowired
    public StartupDataLoader(UserService userService, TournamentService tournamentService)
    {
        this.userService = userService;
        this.tournamentService = tournamentService;
    }

    public void run(ApplicationArguments args) throws SponsorDoesntExistException, DisciplineDoesntExistException, TournamentsFileException
    {
        User user = new User("Franek", "Bolkowski", "asd@dsf.com", "12345");
        user.setEnabled(true);

        userService.saveUser(user);

        user = new User("Janek", "Bolkowski", "test@a.com", "12345");
        user.setEnabled(true);

        userService.saveUser(user);

        user = new User("Janek", "Bolkowski", "test@b.com", "12345");
        user.setEnabled(true);

        userService.saveUser(user);

        tournamentService.registerDiscipline(new DisciplineDTO("Basketball", "images/disciplines/basketball.png"));
        tournamentService.registerDiscipline(new DisciplineDTO("Boxing", "images/disciplines/boxing-gloves.png"));
        tournamentService.registerDiscipline(new DisciplineDTO("Bowling", "images/disciplines/bowling.png"));
        tournamentService.registerDiscipline(new DisciplineDTO("Archery", "images/disciplines/archery.png"));

        tournamentService.registerSponsor(new SponsorDTO("CocaCola", "images/disciplines/basketball.png"));
        tournamentService.registerSponsor(new SponsorDTO("Pepsi", "images/disciplines/basketball.png"));

        TournamentRegistrationDTO tournamentRegistrationDTO = new TournamentRegistrationDTO();
        tournamentRegistrationDTO.setName("Basketball NBA contest");
        tournamentRegistrationDTO.setDeadlineAsLocalDateTime(LocalDateTime.now().plusDays(6));
        tournamentRegistrationDTO.setDescription("Short description of this tournament goes here!");
        tournamentRegistrationDTO.setEntryLimit(7);
        tournamentRegistrationDTO.setLatitude(50.484420);
        tournamentRegistrationDTO.setLongitude(20.180743);
        tournamentRegistrationDTO.setDiscipline("Basketball");
        tournamentRegistrationDTO.setSponsors(List.of("CocaCola"));
        tournamentService.registerTournament(tournamentRegistrationDTO, user);

        tournamentRegistrationDTO.setName("Boxing and kickboxing.");
        tournamentRegistrationDTO.setDeadlineAsLocalDateTime(LocalDateTime.now().plusDays(12));
        tournamentRegistrationDTO.setDescription("Sed placerat felis nec velit laoreet euismod. Vivamus mattis ipsum sit amet elit posuere, quis euismod augue mollis. Pellentesque varius quam vitae eleifend posuere. Mauris ornare mauris sit amet iaculis tincidunt. Nulla porttitor arcu vitae facilisis suscipit. Donec elit tortor, elementum non velit nec, scelerisque tristique magna. !");
        tournamentRegistrationDTO.setLatitude(51.556831);
        tournamentRegistrationDTO.setLongitude(17.054894);
        tournamentRegistrationDTO.setEntryLimit(2);
        tournamentRegistrationDTO.setDiscipline("Boxing");
        tournamentService.registerTournament(tournamentRegistrationDTO, user);

        tournamentRegistrationDTO.setName("It all depends on LUCK...");
        tournamentRegistrationDTO.setDeadlineAsLocalDateTime(LocalDateTime.now().plusDays(30));
        tournamentRegistrationDTO.setDescription("Proin eget massa et felis dignissim egestas. Sed ullamcorper tempus nibh quis ultrices. Nulla ut tincidunt magna. Donec ullamcorper lacinia risus, in ornare tellus ornare nec. Ut lorem lectus, posuere et justo et, euismod semper neque. ");
        tournamentRegistrationDTO.setLatitude(50.484420);
        tournamentRegistrationDTO.setLongitude(20.180743);
        tournamentRegistrationDTO.setEntryLimit(8);
        tournamentRegistrationDTO.setDiscipline("Bowling");
        tournamentService.registerTournament(tournamentRegistrationDTO, user);

        tournamentRegistrationDTO.setName("Roving marks");
        tournamentRegistrationDTO.setDeadlineAsLocalDateTime(LocalDateTime.now().plusDays(2));
        tournamentRegistrationDTO.setDescription("Maecenas quis pharetra leo. Vestibulum vehicula vulputate tortor vel finibus. Donec nec mauris ut tellus tempus molestie eget vitae justo. ");
        tournamentRegistrationDTO.setLatitude(-7.572652);
        tournamentRegistrationDTO.setLongitude(110.823447);
        tournamentRegistrationDTO.setEntryLimit(64);
        tournamentRegistrationDTO.setDiscipline("Archery");
        tournamentRegistrationDTO.setSponsors(List.of("CocaCola", "Pepsi"));
        tournamentService.registerTournament(tournamentRegistrationDTO, user);

        for (int i = 0; i < 15; i++)
        {
            tournamentRegistrationDTO.setName("Roving marks" + i);
            tournamentService.registerTournament(tournamentRegistrationDTO, user);
        }
    }
}
