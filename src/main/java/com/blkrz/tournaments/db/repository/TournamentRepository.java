package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.Tournament;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer>
{
    Page<Tournament> findAllByOrderByNameAsc(Pageable pageable);

    Page<Tournament> findByNameContainingIgnoreCaseOrderByName(String name, Pageable pageable);

    Page<Tournament> findAllByOrderByDeadlineDesc(Pageable pageable);

    Page<Tournament> findByNameContainingIgnoreCaseOrderByDeadlineDesc(String name, Pageable pageable);
}
