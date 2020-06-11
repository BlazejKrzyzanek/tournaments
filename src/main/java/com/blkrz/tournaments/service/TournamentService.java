package com.blkrz.tournaments.service;

import com.blkrz.tournaments.data.TournamentSortingTypeEnum;
import com.blkrz.tournaments.data.dto.*;
import com.blkrz.tournaments.db.model.*;
import com.blkrz.tournaments.db.repository.DisciplineRepository;
import com.blkrz.tournaments.db.repository.SponsorRepository;
import com.blkrz.tournaments.db.repository.TournamentRepository;
import com.blkrz.tournaments.db.repository.UserInTournamentRepository;
import com.blkrz.tournaments.exception.DisciplineDoesntExistException;
import com.blkrz.tournaments.exception.SponsorDoesntExistException;
import com.blkrz.tournaments.exception.TooManyUsersRegisteredToTournament;
import com.blkrz.tournaments.exception.TournamentsFileException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Service
public class TournamentService
{
    private final TournamentRepository tournamentRepository;
    private final DisciplineRepository disciplineRepository;
    private final SponsorRepository sponsorRepository;
    private final UserInTournamentRepository userInTournamentRepository;
    private final FileService fileService;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, DisciplineRepository disciplineRepository, SponsorRepository sponsorRepository, UserInTournamentRepository userInTournamentRepository, FileService fileService)
    {
        this.tournamentRepository = tournamentRepository;
        this.disciplineRepository = disciplineRepository;
        this.sponsorRepository = sponsorRepository;
        this.userInTournamentRepository = userInTournamentRepository;
        this.fileService = fileService;
    }

    public void saveTournament(Tournament tournament)
    {
        tournamentRepository.save(tournament);
    }

    public Tournament registerTournament(TournamentRegistrationDTO tournamentDTO, User organiser) throws DisciplineDoesntExistException, SponsorDoesntExistException, TournamentsFileException
    {
        List<Sponsor> sponsors = getSponsorsFromNames(tournamentDTO.getSponsors());

        SponsorRegistrationDTO newSponsorDTO = tournamentDTO.getNewSponsor();
        if (newSponsorDTO != null)
        {
            Sponsor sponsor = new Sponsor();

            sponsor.setName(newSponsorDTO.getName());

            if (newSponsorDTO.getLogo() != null)
            {
                sponsor.setLogoPath(fileService.saveUploadedImage(newSponsorDTO.getLogo(), newSponsorDTO.getDirectoryPath()));
            }

            sponsorRepository.save(sponsor);
            sponsors.add(sponsor);
        }

        Discipline discipline = getDisciplineByName(tournamentDTO.getDiscipline());

        Tournament tournament = new Tournament();

        tournament.setName(tournamentDTO.getName());
        tournament.setDescription(tournamentDTO.getDescription());
        tournament.setEntryLimit(tournamentDTO.getEntryLimit());
        tournament.setDeadline(tournamentDTO.getDeadlineAsLocalDateTime());
        tournament.setLatitude(tournamentDTO.getLatitude());
        tournament.setLongitude(tournamentDTO.getLongitude());
        tournament.setDiscipline(discipline);
        tournament.setSponsors(sponsors);
        tournament.setOrganiser(organiser);

        return tournamentRepository.save(tournament);
    }

    public List<Sponsor> getSponsorsFromNames(List<String> names) throws SponsorDoesntExistException
    {
        List<Sponsor> sponsors = new ArrayList<>();

        for (String sponsorName : names)
        {
            Sponsor sponsor = sponsorRepository.findByName(sponsorName);
            if (sponsor != null)
            {
                sponsors.add(sponsor);
            }
            else
            {
                throw new SponsorDoesntExistException("Sponsor with such name " + sponsorName + " doesn't exist.");
            }
        }

        return sponsors;
    }

    public Discipline getDisciplineByName(String name) throws DisciplineDoesntExistException
    {
        Discipline discipline = disciplineRepository.findByName(name);
        if (discipline != null)
        {
            return discipline;
        }

        throw new DisciplineDoesntExistException("Discipline with name " + name + " doesn't exist.");
    }

    public void saveSponsor(Sponsor sponsor)
    {
        sponsorRepository.save(sponsor);
    }

    public Sponsor registerSponsor(SponsorDTO sponsorDTO)
    {
        Sponsor sponsor = new Sponsor();
        sponsor.setName(sponsorDTO.getName());
        sponsor.setLogoPath(sponsorDTO.getLogoPath());

        return sponsorRepository.save(sponsor);
    }

    public void saveDiscipline(Discipline discipline)
    {
        disciplineRepository.save(discipline);
    }

    public Discipline registerDiscipline(DisciplineDTO disciplineDTO)
    {
        Discipline discipline = new Discipline();
        discipline.setName(disciplineDTO.getName());
        discipline.setImagePath(disciplineDTO.getImagePath());

        return disciplineRepository.save(discipline);
    }

    public Sponsor getSponsorByName(String name) throws SponsorDoesntExistException
    {
        Sponsor sponsor = sponsorRepository.findByName(name);
        if (sponsor != null)
        {
            return sponsor;
        }

        throw new SponsorDoesntExistException("Sponsor with name " + name + " doesn't exist.");
    }

    public List<DisciplineDTO> getAllDisciplines()
    {
        return disciplineRepository.findAllByOrderByName()
                .stream()
                .map(discipline -> new DisciplineDTO(discipline.getName(), discipline.getImagePath()))
                .collect(Collectors.toList());
    }

    public List<SponsorDTO> getAllSponsors()
    {
        return sponsorRepository.findAllByOrderByName()
                .stream()
                .map(sponsor -> new SponsorDTO(sponsor.getName(), sponsor.getLogoPath()))
                .collect(Collectors.toList());
    }

    public TournamentViewDTO getTournamentViewByName(String tournamentName)
    {
        Tournament tournament = tournamentRepository.findByName(tournamentName);
        List<UserInTournament> classification = userInTournamentRepository.findAllByTournament(tournament);
        TournamentViewDTO viewDTO = new TournamentViewDTO();
        viewDTO.setName(tournament.getName());
        viewDTO.setDescription(tournament.getDescription() != null ? tournament.getDescription() : "");
        viewDTO.setDeadline(tournament.getDeadline());
        viewDTO.setDiscipline(new DisciplineDTO(tournament.getDiscipline()));
        viewDTO.setSponsors(tournament.getSponsors().stream().map(SponsorDTO::new).collect(Collectors.toList()));

        viewDTO.setEntriesCount(classification.size());
        viewDTO.setEntryLimit(tournament.getEntryLimit());

        viewDTO.setIsRegistrationOpen(tournament.getDeadline().isAfter(LocalDateTime.now()) || viewDTO.getEntriesCount() >= viewDTO.getEntryLimit());

        viewDTO.setLatitude(tournament.getLatitude());
        viewDTO.setLongitude(tournament.getLongitude());
        viewDTO.setOrganiserEmail(tournament.getOrganiser().getEmail());

        classification.sort(Comparator.comparingInt(UserInTournament::getRank));
        viewDTO.setClassification(classification.stream().map(ClassificationDTO::new).collect(Collectors.toList()));

        return viewDTO;
    }

    public UserInTournament registerUserToTournament(User user, String tournamentName, String licence, Integer rank) throws TooManyUsersRegisteredToTournament
    {
        Tournament tournament = tournamentRepository.findByName(tournamentName);

        if (userInTournamentRepository.findAllByTournament(tournament).size() < tournament.getEntryLimit())
        {
            UserInTournament userInTournament = new UserInTournament();
            userInTournament.setUser(user);
            userInTournament.setTournament(tournament);
            userInTournament.setLicenceCode(licence);
            userInTournament.setRank(rank);

            return userInTournamentRepository.save(userInTournament);
        }

        throw new TooManyUsersRegisteredToTournament("Too many users attempt to register for this tournament. Registration is now closed.");
    }

    public Boolean isUserRegisteredOnTournament(User user, String tournamentName)
    {
        return null != userInTournamentRepository.getByUserAndTournament(user, tournamentRepository.findByName(tournamentName));
    }

    public TournamentRegistrationDTO getTournamentAsRegistrationDTO(String tournamentName)
    {
        TournamentRegistrationDTO dto = new TournamentRegistrationDTO();
        Tournament tournament = tournamentRepository.findByName(tournamentName);
        if (tournament != null)
        {
            dto.setId(tournament.getId());
            dto.setName(tournamentName);
            dto.setDescription(tournament.getDescription());
            dto.setDeadlineAsLocalDateTime(tournament.getDeadline());
            dto.setLatitude(tournament.getLatitude());
            dto.setLongitude(tournament.getLongitude());
            dto.setDiscipline(tournament.getDiscipline().getName());
            dto.setSponsors(tournament.getSponsors().stream().map(Sponsor::getName).collect(Collectors.toList()));
            dto.setEntryLimit(tournament.getEntryLimit());
        }

        return dto;
    }

    public Tournament updateTournament(TournamentRegistrationDTO dto) throws SponsorDoesntExistException
    {
        Tournament tournament = tournamentRepository.findById(dto.getId()).orElse(null);

        if (tournament != null)
        {
            tournament.setName(dto.getName());
            tournament.setDescription(dto.getDescription());
            tournament.setDeadline(dto.getDeadlineAsLocalDateTime());
            tournament.setLatitude(dto.getLatitude());
            tournament.setLongitude(dto.getLongitude());
            tournament.setDiscipline(disciplineRepository.findByName(dto.getDiscipline()));
            tournament.setSponsors(getSponsorsFromNames(dto.getSponsors()));
            tournament.setEntryLimit(dto.getEntryLimit());

            return tournamentRepository.save(tournament);
        }

        return null;
    }

    public Page<SimpleTournamentDTO> getTournaments(Integer page, Integer pageEntries, String search, String discipline, TournamentSortingTypeEnum sortingType)
    {
        switch (sortingType)
        {
            case DEADLINE_DESC:
                return getTournamentsByDeadline(page, pageEntries, search, discipline);
            case NAME_ASC:
            default:
                return getTournamentsByName(page, pageEntries, search, discipline);
        }
    }

    public Page<SimpleTournamentDTO> getTournamentsByDeadline(int page, int pageEntries, String search, String discipline)
    {
        if (discipline == null || discipline.isEmpty())
        {
            return getTournamentsByDeadline(page, pageEntries, search);
        }
        if (search == null)
        {
            search = "";
        }

        return tournamentRepository
                .findByNameContainingIgnoreCaseAndDisciplineOrderByDeadlineDesc(search, disciplineRepository.findByName(discipline), PageRequest.of(page, pageEntries))
                .map(SimpleTournamentDTO::new);
    }

    public Page<SimpleTournamentDTO> getTournamentsByName(int page, int pageEntries, String search, String discipline)
    {
        if (discipline == null || discipline.isEmpty())
        {
            return getTournamentsByName(page, pageEntries, search);
        }
        if (search == null)
        {
            search = "";
        }

        return tournamentRepository
                .findByNameContainingIgnoreCaseAndDisciplineOrderByName(search, disciplineRepository.findByName(discipline), PageRequest.of(page, pageEntries))
                .map(SimpleTournamentDTO::new);
    }

    public Page<SimpleTournamentDTO> getTournamentsByDeadline(int page, int pageEntries, String search)
    {
        if (search == null || search.isEmpty())
        {
            return getTournamentsByDeadline(page, pageEntries);
        }

        return tournamentRepository
                .findByNameContainingIgnoreCaseOrderByDeadlineDesc(search, PageRequest.of(page, pageEntries))
                .map(SimpleTournamentDTO::new);
    }

    public Page<SimpleTournamentDTO> getTournamentsByName(int page, int pageEntries, String search)
    {
        if (search == null || search.isEmpty())
        {
            return getTournamentsByName(page, pageEntries);
        }

        return tournamentRepository
                .findByNameContainingIgnoreCaseOrderByName(search, PageRequest.of(page, pageEntries))
                .map(SimpleTournamentDTO::new);
    }

    public Page<SimpleTournamentDTO> getTournamentsByDeadline(int page, int pageEntries)
    {
        return tournamentRepository
                .findAllByOrderByDeadlineDesc(PageRequest.of(page, pageEntries))
                .map(SimpleTournamentDTO::new);
    }

    public Page<SimpleTournamentDTO> getTournamentsByName(int page, int pageEntries)
    {
        return tournamentRepository
                .findAllByOrderByNameAsc(PageRequest.of(page, pageEntries))
                .map(SimpleTournamentDTO::new);
    }

    public Page<SimpleTournamentDTO> getTournamentsUserTakePart(User user, Integer page, Integer pageEntries, String search, String discipline)
    {
        search = search != null ? search : "";

        String finalSearch = search;
        Stream<Tournament> tournamentStream = userInTournamentRepository
                .findAllByUser(user)
                .stream()
                .map(UserInTournament::getTournament);

        if (discipline != null && !discipline.isEmpty())
        {
            tournamentStream = tournamentStream.filter(tournament -> tournament.getDiscipline().getName().equals(discipline));
        }

        List<SimpleTournamentDTO> tournamentList = tournamentStream.filter(tournament -> tournament.getName().contains(finalSearch))
                .map(SimpleTournamentDTO::new)
                .collect(Collectors.toList());

        return new PageImpl<>(tournamentList, PageRequest.of(page, pageEntries), tournamentList.size());
    }

    public Page<SimpleTournamentDTO> getTournamentsByOrganiser(User organiser, Integer page, Integer pageEntries, String search, String discipline)
    {
        search = search != null ? search : "";

        if (discipline != null && !discipline.isEmpty())
        {
            return tournamentRepository
                    .findByOrganiserAndNameContainingIgnoreCaseAndDisciplineOrderByName(
                            organiser, search, disciplineRepository.findByName(discipline), PageRequest.of(page, pageEntries))
                    .map(SimpleTournamentDTO::new);
        }

        return tournamentRepository
                .findByOrganiserAndNameContainingIgnoreCaseOrderByName(
                        organiser, search, PageRequest.of(page, pageEntries))
                .map(SimpleTournamentDTO::new);
    }
}
