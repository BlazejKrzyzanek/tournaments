package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.Sponsor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SponsorRepository extends JpaRepository<Sponsor, Integer>
{
}
