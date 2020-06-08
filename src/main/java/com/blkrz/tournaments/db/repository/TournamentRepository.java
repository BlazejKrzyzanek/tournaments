package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer>
{
}
