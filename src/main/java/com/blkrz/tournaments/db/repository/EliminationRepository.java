package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.Elimination;
import com.blkrz.tournaments.db.model.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EliminationRepository extends JpaRepository<Elimination, Integer>
{
    Elimination findByTournament(Tournament tournament);
}
