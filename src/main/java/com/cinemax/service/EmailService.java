package com.cinemax.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class EmailService {
    public void sendResetPasswordEmail(String email, String resetPasswordCode) {

        throw new UnsupportedOperationException("Not implemented yet"); //TODO---
    }
}
