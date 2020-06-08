package com.blkrz.tournaments.db;

import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.db.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupDataLoader implements ApplicationRunner
{

    private final UserRepository userRepository;

    @Autowired
    public StartupDataLoader(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }

    public void run(ApplicationArguments args)
    {
        userRepository.save(new User("Franek", "Bolkowski", "asd@dsf.com", "12345"));
        userRepository.save(new User("Janek", "Jankowski", "mail2131@gmail.com", "12345"));
        userRepository.save(new User("Alek", "Rurrurur", "pururururur@rurur.com", "12345"));
    }
}
