package com.blkrz.tournaments.data.authentication;

import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.service.MailService;
import com.blkrz.tournaments.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

import static com.blkrz.tournaments.data.authentication.VerificationTokenTypeEnum.FORGOT_PASSWORD;

@Component
public class ForgotPasswordListener implements ApplicationListener<OnForgotPasswordEvent>
{
    private final UserService service;
    private final MailService mailService;

    @Autowired
    public ForgotPasswordListener(UserService service, MailService mailService)
    {
        this.service = service;
        this.mailService = mailService;
    }

    @Override
    public void onApplicationEvent(OnForgotPasswordEvent event)
    {
        this.confirmRegistration(event);
    }

    private void confirmRegistration(OnForgotPasswordEvent event)
    {
        User user = event.getUser();
        String token = UUID.randomUUID().toString();
        service.createVerificationToken(user, token, FORGOT_PASSWORD);

        String recipientAddress = user.getEmail();
        String subject = "Reset your password.";
        String resetUrl = event.getAppUrl() + "/user/reset-password?token=" + token;
        String message = "Hello " + user.getFirstName() + "!\nTo reset your password please click in following link:\r\nhttp://localhost:8080" + resetUrl;

        mailService.sendMail(recipientAddress, subject, message);
    }
}
