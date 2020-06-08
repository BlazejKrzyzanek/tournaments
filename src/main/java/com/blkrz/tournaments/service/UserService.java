package com.blkrz.tournaments.service;

import com.blkrz.tournaments.data.authentication.VerificationTokenTypeEnum;
import com.blkrz.tournaments.data.dto.UserRegistrationDTO;
import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.db.model.VerificationToken;
import com.blkrz.tournaments.exception.UserAlreadyExistException;

public interface UserService
{
    User registerNewUserAccount(UserRegistrationDTO userRegistrationDTO) throws UserAlreadyExistException;

    User getUser(String verificationToken, VerificationTokenTypeEnum type);

    VerificationToken getVerificationToken(String VerificationToken, VerificationTokenTypeEnum type);

    void saveRegisteredUser(User user);

    void createVerificationToken(User user, String token, VerificationTokenTypeEnum tokenType);

    User getUserByEmail(String email);
}
