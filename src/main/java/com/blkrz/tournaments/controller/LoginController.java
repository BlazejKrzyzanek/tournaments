package com.blkrz.tournaments.controller;

import com.blkrz.tournaments.data.UserRegistrationDTO;
import com.blkrz.tournaments.data.authentication.OnRegistrationCompleteEvent;
import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.db.repository.UserRepository;
import com.blkrz.tournaments.exception.UserAlreadyExistException;
import com.blkrz.tournaments.service.UserServiceImpl;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;


@SessionAttributes({"currentUser"})
@Controller
public class LoginController
{
    private static final Logger log = LogManager.getLogger(LoginController.class);

    private final UserRepository userRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final UserServiceImpl userService;

    @Autowired
    public LoginController(UserRepository userRepository, UserServiceImpl userService, ApplicationEventPublisher eventPublisher)
    {
        this.userRepository = userRepository;
        this.userService = userService;
        this.eventPublisher = eventPublisher;
    }

    @GetMapping("/user/registration")
    public String showRegistrationForm(WebRequest request, Model model)
    {

        UserRegistrationDTO userDTO = new UserRegistrationDTO();
        model.addAttribute("user", userDTO);
        return "registration";
    }

    @PostMapping("/user/registration")
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDTO userDTO, HttpServletRequest request, Errors errors)
    {
        if (!errors.hasErrors())
        {
            try
            {
                User user = userService.registerNewUserAccount(userDTO);
                String appUrl = request.getContextPath();

                eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user, appUrl));

                return new ModelAndView("redirect:/user/login?registered");

            }
            catch (UserAlreadyExistException uaeEx)
            {
                ModelAndView mav = new ModelAndView("registration");

                mav.addObject("message", "An account for that email already exists.");
                mav.addObject("user", userDTO);
                return mav;
            }
        }

        return new ModelAndView("registration", "user", userDTO);
    }

    @GetMapping(value = "/user/login")
    public String login()
    {
        return "login";
    }
}
