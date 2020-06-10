package com.blkrz.tournaments.controller;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;

@Controller
public class ErrorControllerImpl implements ErrorController
{
    @RequestMapping("/error")
    public String handleError(Model model, HttpServletRequest request)
    {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);

        String message = "Sorry! Something went wrong.";

        if (status != null)
        {
            int statusCode = Integer.parseInt(status.toString());

            if (statusCode == HttpStatus.NOT_FOUND.value())
            {
                message = "The page you are looking for was not found.";
            }
            else if (statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value())
            {
                message = "Server error occurred!";
            }

            model.addAttribute("status", statusCode);
        }

        model.addAttribute("message", message);

        return "error";
    }

    @Override
    public String getErrorPath()
    {
        return "/error";
    }
}
