package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.Discipline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisciplineRepository extends JpaRepository<Discipline, Integer>
{
    Discipline findByName(String name);
    List<Discipline> findAllByOrderByName();
}
