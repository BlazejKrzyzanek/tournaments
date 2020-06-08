package com.blkrz.tournaments.db.repository;

import com.blkrz.tournaments.db.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>
{
    User findByEmail(String email);
}
