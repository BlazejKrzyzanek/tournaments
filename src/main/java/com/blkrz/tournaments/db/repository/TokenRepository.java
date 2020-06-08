package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<Token, Integer>
{
    Token findByToken(String token);
}
