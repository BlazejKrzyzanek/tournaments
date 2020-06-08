package com.blkrz.tournaments;

import com.ulisesbocchio.jasyptspringboot.annotation.EnableEncryptableProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableEncryptableProperties
@SpringBootApplication
public class TournamentsApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(TournamentsApplication.class, args);
    }
}
