package com.blkrz.tournaments.db.model;

import com.blkrz.tournaments.data.authentication.VerificationTokenTypeEnum;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;

@Data
@Entity
public class VerificationToken
{
    private static final int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue
    private Long id;

    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    @Enumerated(value = EnumType.STRING)
    private VerificationTokenTypeEnum type;

    public VerificationToken()
    {
    }

    public VerificationToken(String token, User user, VerificationTokenTypeEnum type)
    {
        this.token = token;
        this.user = user;
        this.type = type;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Timestamp(cal.getTime().getTime()));
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }
}


