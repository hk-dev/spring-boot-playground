package com.hkdev.backend.service;

import com.hkdev.web.domain.frontend.ContactPojo;
import org.springframework.mail.SimpleMailMessage;

public interface EmailService {

    void sendEmail(ContactPojo contactPojo);

    void sendGenericEmailMessage(SimpleMailMessage message);
}
