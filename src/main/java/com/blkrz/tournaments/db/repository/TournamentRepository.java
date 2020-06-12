package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.Discipline;
import com.blkrz.tournaments.db.model.Tournament;
import com.blkrz.tournaments.db.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TournamentRepository extends JpaRepository<Tournament, Integer>
{
    Page<Tournament> findAllByOrderByNameAsc(Pageable pageable);

    Page<Tournament> findByNameContainingIgnoreCaseOrderByName(String name, Pageable pageable);

    Page<Tournament> findAllByOrderByDeadlineDesc(Pageable pageable);

    Page<Tournament> findByNameContainingIgnoreCaseOrderByDeadlineDesc(String name, Pageable pageable);

    Tournament findByName(String name);

    Page<Tournament> findByNameContainingIgnoreCaseAndDisciplineOrderByDeadlineDesc(String name, Discipline discipline, Pageable pageable);

    Page<Tournament> findByNameContainingIgnoreCaseAndDisciplineOrderByName(String name, Discipline discipline, Pageable pageable);

    Page<Tournament> findByOrganiserAndNameContainingIgnoreCaseAndDisciplineOrderByName(User organiser, String name, Discipline discipline, Pageable pageable);

    Page<Tournament> findByOrganiserAndNameContainingIgnoreCaseOrderByName(User organiser, String search, Pageable pageable);

    List<Tournament> findAllByEliminationIsNullAndDeadlineBetween(LocalDateTime from, LocalDateTime to);
}
