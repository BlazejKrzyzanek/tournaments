package com.blkrz.tournaments.service;

public interface MailService
{
    void sendMail(String to, String subject, String text);
}
