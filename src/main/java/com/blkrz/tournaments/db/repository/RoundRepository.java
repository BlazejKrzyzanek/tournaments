package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.Round;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepository extends JpaRepository<Round, Integer>
{
}
