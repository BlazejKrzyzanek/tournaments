package com.blkrz.tournaments.db.model;

import lombok.Data;
import org.hibernate.annotations.NaturalId;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Entity
public class User
{
    @Id
    @GeneratedValue
    private Integer id;
    @NotNull
    private String firstName;
    @NotNull
    private String lastName;
    @NotNull
    @NaturalId
    @Email
    private String email;
    @NotNull
    private Boolean enabled;
    @NotNull
    private String password;
    @OneToMany(mappedBy = "organiser", cascade = CascadeType.ALL,
            fetch = FetchType.LAZY)
    private List<Tournament> organisedTournaments;
    @NotNull
    private String role;

    public User()
    {
    }

    public User(String firstName, String lastName, String email, String password)
    {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.enabled = true;
        this.password = encodePassword(password);
        this.role = "ROLE_USER";
    }

    private String encodePassword(String password)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        return passwordEncoder.encode(password);
    }

    public void setPassword(String password)
    {
        this.password = encodePassword(password);
    }
}
