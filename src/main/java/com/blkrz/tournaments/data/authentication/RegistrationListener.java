package com.blkrz.tournaments.data.authentication;

import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.service.MailService;
import com.blkrz.tournaments.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.blkrz.tournaments.data.authentication.VerificationTokenTypeEnum.REGISTRATION;

@Component
public class RegistrationListener implements ApplicationListener<OnRegistrationCompleteEvent>
{

    private final UserService service;
    private final MailService mailService;

    @Autowired
    public RegistrationListener(UserService service, MailService mailService)
    {
        this.service = service;
        this.mailService = mailService;
    }

    @Override
    public void onApplicationEvent(OnRegistrationCompleteEvent event)
    {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnRegistrationCompleteEvent event)
    {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token, REGISTRATION);

        String recipientAddress = user.getEmail();
        String subject = "Complete your registration.";
        String confirmationUrl = event.getAppUrl() + "/user/registration-confirmation?token=" + token;
        String message = "To complete your registration click in following link:\r\nhttp://localhost:8080" + confirmationUrl;

        mailService.sendMail(recipientAddress, subject, message);
    }
}
