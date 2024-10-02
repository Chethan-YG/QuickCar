package com.quickcar.rent.service;

import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class OtpService {
    private final Map<String, String> otpStore = new ConcurrentHashMap<>();

    public void storeOtp(String email, String otp) {
        otpStore.put(email, otp);
    }

    public String getOtp(String email) {
        return otpStore.get(email);
    }

    public void removeOtp(String email) {
        otpStore.remove(email);
    }
}
