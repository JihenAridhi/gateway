package com.uib.gateway.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class EmailService {

    @Value("${spring.mail.username}") 
    private String sender;

    @Autowired 
    private JavaMailSender javaMailSender;

    public String sendMail(String recepient, String subject, String text)
    {
        try
        {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setFrom(sender);
            mail.setTo(recepient);
            mail.setSubject(subject);
            mail.setText(text);

            javaMailSender.send(mail);
            
            return "success";
        }
        catch(Exception e)
        {return "email failed: " + e.getClass().toString() + ":\n" + e.getMessage();}
    }

}
