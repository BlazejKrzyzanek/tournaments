package com.blkrz.tournaments.service;

import com.blkrz.tournaments.data.UserRegistrationDTO;
import com.blkrz.tournaments.db.model.Token;
import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.db.repository.TokenRepository;
import com.blkrz.tournaments.db.repository.UserRepository;
import com.blkrz.tournaments.exception.UserAlreadyExistException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService
{
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, TokenRepository tokenRepository)
    {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    @Transactional
    @Override
    public User registerNewUserAccount(UserRegistrationDTO userRegistrationDTO) throws UserAlreadyExistException
    {
        if (emailExists(userRegistrationDTO.getEmail()))
        {
            throw new UserAlreadyExistException(
                    String.format("User with this email already exists: %s", userRegistrationDTO.getEmail()));
        }

        User user = new User();
        user.setFirstName(userRegistrationDTO.getFirstName());
        user.setLastName(userRegistrationDTO.getLastName());
        user.setEmail(userRegistrationDTO.getEmail());
        user.setEnabled(false);
        user.setRole("ROLE_USER");
        user.setPassword(userRegistrationDTO.getPassword());

        return userRepository.save(user);
    }

    private boolean emailExists(String email)
    {
        return userRepository.findByEmail(email) != null;
    }

    @Override
    public User getUser(String verificationToken)
    {
        return tokenRepository.findByToken(verificationToken).getUser();
    }

    @Override
    public Token getVerificationToken(String VerificationToken) {
        return tokenRepository.findByToken(VerificationToken);
    }

    @Override
    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }

    @Override
    public void createVerificationToken(User user, String token) {
        Token myToken = new Token(token, user);
        tokenRepository.save(myToken);
    }
}
