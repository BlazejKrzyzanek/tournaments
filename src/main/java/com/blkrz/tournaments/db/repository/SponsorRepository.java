package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Integer>
{
    Sponsor findByName(String sponsorName);
    List<Sponsor> findAllByOrderByName();
}
