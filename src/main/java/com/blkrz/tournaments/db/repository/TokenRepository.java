package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.data.authentication.VerificationTokenTypeEnum;
import com.blkrz.tournaments.db.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenRepository extends JpaRepository<VerificationToken, Integer>
{
    VerificationToken findByTokenAndTypeOrderByExpiryDateDesc(String token, VerificationTokenTypeEnum type);
}
