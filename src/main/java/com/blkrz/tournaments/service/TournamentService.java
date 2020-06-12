package com.blkrz.tournaments.service;

import com.blkrz.tournaments.data.TournamentSortingTypeEnum;
import com.blkrz.tournaments.data.dto.*;
import com.blkrz.tournaments.db.model.*;
import com.blkrz.tournaments.db.repository.*;
import com.blkrz.tournaments.exception.DisciplineDoesntExistException;
import com.blkrz.tournaments.exception.SponsorDoesntExistException;
import com.blkrz.tournaments.exception.TooManyUsersRegisteredToTournament;
import com.blkrz.tournaments.exception.TournamentsFileException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Transactional
@Service
public class TournamentService
{
    private static final Logger logger = LogManager.getLogger(TournamentService.class);

    private final TournamentRepository tournamentRepository;
    private final DisciplineRepository disciplineRepository;
    private final SponsorRepository sponsorRepository;
    private final UserInTournamentRepository userInTournamentRepository;
    private final FileService fileService;
    private final EliminationRepository eliminationRepository;
    private final RoundRepository roundRepository;
    private final DuelRepository duelRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, DisciplineRepository disciplineRepository, SponsorRepository sponsorRepository, UserInTournamentRepository userInTournamentRepository, FileService fileService, EliminationRepository eliminationRepository, RoundRepository roundRepository, DuelRepository duelRepository)
    {
        this.tournamentRepository = tournamentRepository;
        this.disciplineRepository = disciplineRepository;
        this.sponsorRepository = sponsorRepository;
        this.userInTournamentRepository = userInTournamentRepository;
        this.fileService = fileService;
        this.eliminationRepository = eliminationRepository;
        this.roundRepository = roundRepository;
        this.duelRepository = duelRepository;
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
        List<UserInTournament> classification = userInTournamentRepository.findAllByTournamentOrderByRank(tournament);
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

        viewDTO.setClassification(classification.stream().map(ClassificationDTO::new).collect(Collectors.toList()));

        return viewDTO;
    }

    public UserInTournament registerUserToTournament(User user, String tournamentName, String licence, Integer rank) throws TooManyUsersRegisteredToTournament
    {
        Tournament tournament = tournamentRepository.findByName(tournamentName);

        if (userInTournamentRepository.findAllByTournamentOrderByRank(tournament).size() < tournament.getEntryLimit())
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

    public List<RoundDTO> getEliminationRoundsOfTournament(String tournamentName)
    {
        Elimination elimination = eliminationRepository.findByTournament(tournamentRepository.findByName(tournamentName));
        if (elimination != null)
        {
            List<Round> rounds = elimination.getRounds();

            return rounds.stream().map(round -> {
                RoundDTO roundDTO = new RoundDTO();
                roundDTO.setNumber(round.getRoundNumber());
                roundDTO.setDuels(round.getDuels().stream().map(duel ->
                {
                    DuelDTO duelDTO = new DuelDTO(duel);
                    return duelDTO;
                })
                        .collect(Collectors.toList()));
                return roundDTO;
            }).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    /** Check for finished registration every 3 minutes. */
    @Scheduled(fixedDelay = 180000)
    public void calculateElimination()
    {
        logger.info("Tournament elimination calculation START.");

        List<Elimination> eliminationsToAdd = new ArrayList<>();
        List<Duel> duelsToAdd = new ArrayList<>();
        List<Round> roundsToAdd = new ArrayList<>();
        List<Tournament> tournamentsToAdd = new ArrayList<>();

        List<Tournament> tournamentsToCalculate = tournamentRepository.findAllByEliminationIsNullAndDeadlineBetween(LocalDateTime.now().minusMinutes(20), LocalDateTime.now());
        for (Tournament tournament : tournamentsToCalculate)
        {
            Elimination elimination = new Elimination();
            elimination.setTournament(tournament);

            eliminationsToAdd.add(elimination);

            Deque<User> allUsersInTournament = userInTournamentRepository
                    .findAllByTournamentOrderByRank(tournament)
                    .stream().map(UserInTournament::getUser)
                    .collect(Collectors.toCollection(ArrayDeque::new));

            int howManyVirtual = 0;
            // Adding virtual user to have proper count of users
            while (!isPowerOfTwo(allUsersInTournament.size() + howManyVirtual))
            {
                howManyVirtual++;
            }

            int initialRoundSize = (allUsersInTournament.size() + howManyVirtual) / 2;

            logger.info("How many virtual users: " + howManyVirtual);
            logger.info("Initial round size: " + initialRoundSize);

            Round round1 = new Round();
            round1.setElimination(elimination);
            round1.setRoundNumber(0);

            roundsToAdd.add(round1);

            int duelNo = 0;
            while (allUsersInTournament.size() + howManyVirtual > 0)
            {
                Duel duel = new Duel();
                duel.setRound(round1);
                duel.setNumber(duelNo++);
                duel.setFirst(allUsersInTournament.pollFirst());
                if (howManyVirtual > 0)
                {
                    duel.setSecond(null);
                    howManyVirtual--;
                }
                else
                {
                    duel.setSecond(allUsersInTournament.pollLast());
                }

                duel.setWinner(null);

                duelsToAdd.add(duel);
            }

            for (int roundUsersCount = initialRoundSize / 2, i = 1;
                 roundUsersCount > 0;
                 roundUsersCount /= 2, i++)
            {
                logger.info("Round: " + i);
                Round round = new Round();
                round.setElimination(elimination);
                round.setRoundNumber(i);

                roundsToAdd.add(round);

                for (int j = 0, n = 0; j < roundUsersCount; j++, n++)
                {
                    Duel duel = new Duel();
                    duel.setRound(round);
                    duel.setNumber(n);
                    duel.setFirst(null);
                    duel.setSecond(null);
                    duel.setWinner(null);

                    duelsToAdd.add(duel);
                }
            }
            tournament.setElimination(elimination);
            tournamentsToAdd.add(tournament);
        }

        if (!eliminationsToAdd.isEmpty())
        {
            eliminationRepository.saveAll(eliminationsToAdd);
            roundRepository.saveAll(roundsToAdd);
            duelRepository.saveAll(duelsToAdd);
            tournamentRepository.saveAll(tournamentsToAdd);
        }

        logger.info("Tournament elimination calculation END.");
    }

    private static boolean isPowerOfTwo(int number)
    {
        return (number > 0) && ((number & (number - 1)) == 0);
    }

    public DuelDTO getDuelByUserAndTournament(User user, String tournamentName)
    {
        Elimination elimination = eliminationRepository.findByTournament(tournamentRepository.findByName(tournamentName));
        if (elimination != null)
        {
            Duel any = elimination.getRounds()
                    .stream()
                    .flatMap(round -> round.getDuels().stream())
                    .filter(Objects::nonNull)
                    .filter(duel -> duel.getWinner() == null)
                    .filter(duel -> user.equals(duel.getFirst()) || user.equals(duel.getSecond()))
                    .findAny()
                    .orElse(null);

            return new DuelDTO(any);
        }
        return new DuelDTO(null);
    }

    public void saveWinnerOfDuel(String tournamentName, User user)
    {
        Elimination elimination = eliminationRepository.findByTournament(tournamentRepository.findByName(tournamentName));

        if (elimination != null)
        {
            Duel any = elimination.getRounds()
                    .stream()
                    .flatMap(round -> round.getDuels().stream())
                    .filter(Objects::nonNull)
                    .filter(duel -> duel.getWinner() == null)
                    .filter(duel -> user.equals(duel.getFirst()) || user.equals(duel.getSecond()))
                    .findAny()
                    .orElse(null);

            if (any != null)
            {
                any.setWinner(user);
                duelRepository.save(any);

                Integer duelNumber = any.getNumber();
                Integer roundNumber = any.getRound().getRoundNumber();

                logger.info("DuelNo: " + duelNumber + " Round no: " + roundNumber + " Next: " + (int) (duelNumber / 2.0));

                Optional<Round> any1 = elimination.getRounds().stream().filter(o -> o.getRoundNumber().equals(roundNumber + 1)).findAny();

                if (any1.isPresent())
                {
                    Round round = any1.get();
                    Optional<Duel> any2 = round.getDuels().stream().filter(o -> o.getNumber().equals((int) (duelNumber / 2.0))).findAny();

                    if (any2.isPresent())
                    {
                        Duel duel = any2.get();
                        if (duelNumber % 2 == 0)
                        {
                            duel.setFirst(user);
                        }
                        else
                        {
                            duel.setSecond(user);
                        }

                        duelRepository.save(duel);
                    }
                }
            }
        }
    }
}
