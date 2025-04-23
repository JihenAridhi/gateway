package com.uib.gateway.Entities;

import java.time.Duration;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import com.eatthepath.otp.TimeBasedOneTimePasswordGenerator;

import lombok.Data;

@Data
public class TOTP {

    private SecretKey key;
    private TimeBasedOneTimePasswordGenerator code;
    private String email;

    public TOTP()
    {
        this.code = new TimeBasedOneTimePasswordGenerator(Duration.ofSeconds(150));
        try {
            this.key = KeyGenerator.getInstance(code.getAlgorithm()).generateKey();
        } catch (Exception e) {
           this.key = null;
        }
    }
}
