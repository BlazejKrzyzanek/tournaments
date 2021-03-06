package com.blkrz.tournaments.data.authentication;

import com.blkrz.tournaments.db.model.User;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;

@Getter
@Setter
public class OnForgotPasswordEvent extends ApplicationEvent
{
    private String appUrl;
    private User user;

    public OnForgotPasswordEvent(User user, String appUrl)
    {
        super(user);

        this.user = user;
        this.appUrl = appUrl;
    }
}
