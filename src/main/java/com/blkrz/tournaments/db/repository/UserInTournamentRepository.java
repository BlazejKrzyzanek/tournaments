package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.UserInTournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInTournamentRepository extends JpaRepository<UserInTournament, Integer>
{
}
