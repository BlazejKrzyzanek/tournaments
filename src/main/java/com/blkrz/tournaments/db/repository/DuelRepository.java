package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.Duel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DuelRepository extends JpaRepository<Duel, Integer>
{
}
