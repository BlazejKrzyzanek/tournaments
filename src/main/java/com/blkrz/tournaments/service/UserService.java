package com.blkrz.tournaments.service;

import com.blkrz.tournaments.data.UserRegistrationDTO;
import com.blkrz.tournaments.db.model.Token;
import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.exception.UserAlreadyExistException;

public interface UserService
{
    User registerNewUserAccount(UserRegistrationDTO userRegistrationDTO) throws UserAlreadyExistException;

    User getUser(String verificationToken);

    Token getVerificationToken(String VerificationToken);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token);
}
