package com.hkdev.backend.service;

import com.hkdev.backend.persistence.domain.backend.PasswordResetToken;
import com.hkdev.backend.persistence.domain.backend.User;
import com.hkdev.backend.persistence.repositories.PasswordResetTokenRepository;
import com.hkdev.backend.persistence.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class PasswordResetTokenService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${token.expiration.length.minutes}")
    private int tokenExpirationInMinutes;

    private static final Logger LOGGER = LoggerFactory.getLogger(PasswordResetTokenService.class);

    public PasswordResetToken findByToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Transactional
    public PasswordResetToken createPasswordResetTokenForEmail(String email) {
        PasswordResetToken passwordResetToken = null;

        User user = userRepository.findByEmail(email);

        if(user != null) {
            String token = UUID.randomUUID().toString();
            passwordResetToken = new PasswordResetToken(token, user, LocalDateTime.now(Clock.systemUTC()), tokenExpirationInMinutes);
        } else {
            LOGGER.warn("Couldn't find a user for the given email {}", email);
        }

        return passwordResetToken;
    }
}
