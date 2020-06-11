package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.Tournament;
import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.db.model.UserInTournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInTournamentRepository extends JpaRepository<UserInTournament, Integer>
{
    List<UserInTournament> findAllByTournament(Tournament tournament);
    List<UserInTournament> findAllByUser(User tournament);
    UserInTournament getByUserAndTournament(User user, Tournament tournament);
}
