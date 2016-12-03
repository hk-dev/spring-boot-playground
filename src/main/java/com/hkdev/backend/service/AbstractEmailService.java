package com.hkdev.backend.service;

import com.hkdev.web.domain.frontend.ContactPojo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

public abstract class AbstractEmailService implements EmailService {

    @Value("${default.to.address}")
    private String defaultToAddress;

    protected SimpleMailMessage prepareSimpleMailMessageFromFeedback(ContactPojo contactPojo) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(defaultToAddress);
        message.setFrom(contactPojo.getEmail());
        message.setSubject("[HK Dev]: Message received from " + contactPojo.getFirstName() + " " + contactPojo.getLastName());
        message.setText(contactPojo.getFeedback());
        return message;
    }

    @Override
    public void sendEmail(ContactPojo contactPojo) {
        sendGenericEmailMessage(prepareSimpleMailMessageFromFeedback(contactPojo));
    }
}
