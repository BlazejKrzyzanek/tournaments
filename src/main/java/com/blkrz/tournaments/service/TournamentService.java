package com.blkrz.tournaments.service;

import com.blkrz.tournaments.data.TournamentSortingTypeEnum;
import com.blkrz.tournaments.data.dto.DisciplineDTO;
import com.blkrz.tournaments.data.dto.SimpleTournamentDTO;
import com.blkrz.tournaments.data.dto.SponsorDTO;
import com.blkrz.tournaments.data.dto.TournamentRegistrationDTO;
import com.blkrz.tournaments.db.model.Discipline;
import com.blkrz.tournaments.db.model.Sponsor;
import com.blkrz.tournaments.db.model.Tournament;
import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.db.repository.DisciplineRepository;
import com.blkrz.tournaments.db.repository.SponsorRepository;
import com.blkrz.tournaments.db.repository.TournamentRepository;
import com.blkrz.tournaments.exception.DisciplineDoesntExistException;
import com.blkrz.tournaments.exception.SponsorDoesntExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Transactional
@Service
public class TournamentService
{
    private final TournamentRepository tournamentRepository;
    private final DisciplineRepository disciplineRepository;
    private final SponsorRepository sponsorRepository;

    @Autowired
    public TournamentService(TournamentRepository tournamentRepository, DisciplineRepository disciplineRepository, SponsorRepository sponsorRepository)
    {
        this.tournamentRepository = tournamentRepository;
        this.disciplineRepository = disciplineRepository;
        this.sponsorRepository = sponsorRepository;
    }

    public Page<SimpleTournamentDTO> getTournaments(Integer page, Integer pageEntries, String search, TournamentSortingTypeEnum sortingType)
    {
        if (page == null)
        {
            page = 0;
        }
        if (pageEntries == null)
        {
            pageEntries = 10;
        }
        else if (pageEntries > 250)
        {
            pageEntries = 250;
        }

        switch (sortingType)
        {
            case DEADLINE_DESC:
                return getTournamentsByDeadlineAndSearchQuery(page, pageEntries, search);
            case NAME_ASC:
            default:
                return getTournamentsByNameAndSearchQuery(page, pageEntries, search);
        }
    }

    public Page<SimpleTournamentDTO> getTournamentsByDeadlineAndSearchQuery(int page, int pageEntries, String search)
    {
        if (search == null || search.isEmpty())
        {
            return getTournamentsByDeadline(page, pageEntries);
        }

        return tournamentRepository
                .findByNameContainingIgnoreCaseOrderByDeadlineDesc(search, PageRequest.of(page, pageEntries))
                .map(SimpleTournamentDTO::new);
    }

    public Page<SimpleTournamentDTO> getTournamentsByNameAndSearchQuery(int page, int pageEntries, String search)
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

    public void saveTournament(Tournament tournament)
    {
        tournamentRepository.save(tournament);
    }

    public Tournament registerTournament(TournamentRegistrationDTO tournamentDTO, User organiser) throws DisciplineDoesntExistException, SponsorDoesntExistException
    {
        List<Sponsor> sponsors = getSponsorsFromNames(tournamentDTO.getSponsors());
        Discipline discipline = getDisciplineByName(tournamentDTO.getDiscipline());

        Tournament tournament = new Tournament();

        tournament.setName(tournamentDTO.getName());
        tournament.setDescription(tournamentDTO.getDescription());
        tournament.setEntryLimit(tournamentDTO.getEntryLimit());
        tournament.setDeadline(tournamentDTO.getDeadline());
        tournament.setOnline(tournamentDTO.getOnline());
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
        return disciplineRepository.findAll()
                .stream()
                .map(discipline -> new DisciplineDTO(discipline.getName(), discipline.getImagePath()))
                .collect(Collectors.toList());
    }
}
