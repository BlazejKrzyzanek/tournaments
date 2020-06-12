package com.blkrz.tournaments.service;

import com.blkrz.tournaments.data.authentication.VerificationTokenTypeEnum;
import com.blkrz.tournaments.data.dto.UserRegistrationDTO;
import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.db.model.VerificationToken;
import com.blkrz.tournaments.db.repository.TokenRepository;
import com.blkrz.tournaments.db.repository.UserRepository;
import com.blkrz.tournaments.exception.UserAlreadyExistException;
import com.blkrz.tournaments.exception.UserNotLoggedInException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Transactional
@Service
public class UserService
{
    private static final Logger logger = LogManager.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;

    @Autowired
    public UserService(UserRepository userRepository, TokenRepository tokenRepository)
    {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
    }

    public User registerUser(UserRegistrationDTO userRegistrationDTO) throws UserAlreadyExistException
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

        User save = userRepository.save(user);

        logger.log(Level.DEBUG, "User " + user.getEmail() + " registered successfully.");
        return save;
    }

    private boolean emailExists(String email)
    {
        return userRepository.findByEmail(email) != null;
    }

    public User getUser(String verificationToken, VerificationTokenTypeEnum type)
    {
        return tokenRepository.findByTokenAndTypeOrderByExpiryDateDesc(verificationToken, type).getUser();
    }

    public VerificationToken getVerificationToken(String VerificationToken, VerificationTokenTypeEnum type)
    {
        return tokenRepository.findByTokenAndTypeOrderByExpiryDateDesc(VerificationToken, type);
    }

    public void saveUser(User user)
    {
        userRepository.save(user);
    }

    public void createVerificationToken(User user, String token, VerificationTokenTypeEnum tokenType)
    {
        VerificationToken myVerificationToken = new VerificationToken(token, user, tokenType);
        tokenRepository.save(myVerificationToken);
    }

    public boolean isUserLogged()
    {
        try
        {
            getLoggedInUserEmail();
            return true;
        }
        catch (UserNotLoggedInException e)
        {
            return false;
        }
    }

    public String getLoggedInUserEmail() throws UserNotLoggedInException
    {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails)
        {
            return ((UserDetails) principal).getUsername();
        }
        else
        {
            throw new UserNotLoggedInException("Can't get user from session.");
        }
    }

    public User getLoggedInUser() throws UserNotLoggedInException
    {
        return getUserByEmail(getLoggedInUserEmail());
    }

    public User getUserByEmail(String email)
    {
        return userRepository.findByEmail(email);
    }

    public User getUserById(Integer userId)
    {
        return userRepository.getOne(userId);
    }
}
