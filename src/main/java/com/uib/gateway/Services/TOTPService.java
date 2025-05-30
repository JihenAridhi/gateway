package com.uib.gateway.Services;

import java.security.InvalidKeyException;
import java.time.Duration;
import java.time.Instant;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;
import com.uib.gateway.Entities.TOTP;

public class TOTPService {

    TOTP totp;

    public String generateTOTP(String email)
    {
        totp = new TOTP();
        try {
            totp.setEmail(email);
            return totp.getCode().generateOneTimePasswordString(totp.getKey(), Instant.now());
        } catch (Exception e) {
            return e.getMessage();
        }
    }



    public boolean verifyTOTP(String email, String code)
    {
        try {
            return totp.getCode().generateOneTimePasswordString(totp.getKey(), Instant.now()).equals(code) && totp.getEmail().equals(email);
        } catch (InvalidKeyException e) {
            // TODO Auto-generated catch block

            e.getMessage();
            return false;
        }
        
    }

}
