package com.blkrz.tournaments.controller;

import com.blkrz.tournaments.data.authentication.OnForgotPasswordEvent;
import com.blkrz.tournaments.data.authentication.OnRegistrationCompleteEvent;
import com.blkrz.tournaments.data.dto.ForgotPasswordEmailDTO;
import com.blkrz.tournaments.data.dto.ResetPasswordDTO;
import com.blkrz.tournaments.data.dto.UserRegistrationDTO;
import com.blkrz.tournaments.db.model.User;
import com.blkrz.tournaments.db.model.VerificationToken;
import com.blkrz.tournaments.exception.UserAlreadyExistException;
import com.blkrz.tournaments.service.UserService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Calendar;

import static com.blkrz.tournaments.data.authentication.VerificationTokenTypeEnum.FORGOT_PASSWORD;
import static com.blkrz.tournaments.data.authentication.VerificationTokenTypeEnum.REGISTRATION;

@SessionAttributes({"currentUser"})
@Controller
public class LoginController
{
    private static final Logger logger = LogManager.getLogger(LoginController.class);

    private final ApplicationEventPublisher eventPublisher;
    private final UserService userService;

    @Autowired
    public LoginController(UserService userService, ApplicationEventPublisher eventPublisher)
    {
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
    public ModelAndView registerUserAccount(@ModelAttribute("user") @Valid UserRegistrationDTO userDTO, BindingResult bindingResult, HttpServletRequest request)
    {
        if (!bindingResult.hasErrors())
        {
            try
            {
                User user = userService.registerUser(userDTO);

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

    @GetMapping("/user/registration-confirmation")
    public String confirmRegistration(Model model, @RequestParam("token") String token)
    {
        VerificationToken verificationToken = userService.getVerificationToken(token, REGISTRATION);
        if (verificationToken == null)
        {
            model.addAttribute("message", "Invalid verification link.");
            return "verification";
        }

        User user = verificationToken.getUser();
        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiryDate().getTime() - cal.getTime().getTime()) <= 0)
        {
            model.addAttribute("message", "Verification link is expired.");
            return "verification";
        }

        user.setEnabled(true);
        userService.saveUser(user);
        return "redirect:/user/login?verified";
    }

    @GetMapping(value = "/user/login")
    public String login()
    {
        return "login";
    }

    @GetMapping(value = "/user/forgot-password")
    public String forgotPassword(WebRequest request, Model model)
    {
        ForgotPasswordEmailDTO forgotPasswordEmailDTO = new ForgotPasswordEmailDTO();
        model.addAttribute("emailDto", forgotPasswordEmailDTO);
        return "forgotPassword";
    }

    @PostMapping(value = "/user/forgot-password")
    public ModelAndView forgotPassword(@ModelAttribute("emailDto") @Valid ForgotPasswordEmailDTO emailDTO, BindingResult bindingResult, HttpServletRequest request)
    {
        ModelAndView modelAndView = new ModelAndView("forgotPassword", "emailDto", emailDTO);

        if (!bindingResult.hasErrors())
        {
            User user = userService.getUserByEmail(emailDTO.getEmail());

            if (user != null)
            {
                String appUrl = request.getContextPath();
                eventPublisher.publishEvent(new OnForgotPasswordEvent(user, appUrl));

                modelAndView.addObject("success", "Password reset email sent.");
                return modelAndView;
            }
            modelAndView.addObject("error", "User with such email doesn't exist.");
            return modelAndView;
        }

        return modelAndView;
    }

    @GetMapping(value = "/user/reset-password")
    public String resetPassword(Model model, @RequestParam("token") String token)
    {
        ResetPasswordDTO resetPasswordDTO = new ResetPasswordDTO();
        model.addAttribute("resetPasswordDto", resetPasswordDTO);

        VerificationToken verificationToken = userService.getVerificationToken(token, FORGOT_PASSWORD);
        if (verificationToken == null)
        {
            model.addAttribute("message", "Invalid reset password link.");
            return "setNewPassword";
        }

        return "setNewPassword";
    }

    @PostMapping(value = "/user/reset-password")
    public ModelAndView resetPassword(@ModelAttribute("resetPasswordDto") @Valid ResetPasswordDTO resetPasswordDTO, BindingResult bindingResult, HttpServletRequest request)
    {
        ModelAndView mav = new ModelAndView("setNewPassword");

        VerificationToken verificationToken = userService.getVerificationToken(resetPasswordDTO.getToken(), FORGOT_PASSWORD);
        if (verificationToken == null)
        {
            mav.addObject("message", "Invalid reset password link.");
            return mav;
        }

        if (!bindingResult.hasErrors())
        {
            User user = verificationToken.getUser();
            if (user.getEmail().equals(resetPasswordDTO.getEmail()))
            {
                user.setPassword(resetPasswordDTO.getPassword());
                userService.saveUser(user);

                mav = new ModelAndView("login");
                mav.addObject("successMessage", "Your password has been updated!");
                return mav;
            }
        }

        mav.addObject("message", "Invalid reset password link.");
        return mav;
    }

}
