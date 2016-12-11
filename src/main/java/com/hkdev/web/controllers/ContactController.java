package com.hkdev.web.controllers;

import com.hkdev.backend.service.EmailService;
import com.hkdev.web.domain.frontend.ContactPojo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ContactController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ContactController.class);
    private static final String CONTACT_MODEL_KEY = "contact";
    private static final String CONTACT_US_VIEW_NAME = "contact/contact";

    @Autowired
    private EmailService emailService;

    @RequestMapping(value = "/contact", method = RequestMethod.GET)
    public String contactGet(ModelMap model) {
        ContactPojo contactPojo = new ContactPojo();
        model.addAttribute(ContactController.CONTACT_MODEL_KEY, contactPojo);
        return ContactController.CONTACT_US_VIEW_NAME;
    }

    @RequestMapping(value = "/contact", method = RequestMethod.POST)
    public String contactPost(@ModelAttribute(CONTACT_MODEL_KEY) ContactPojo contactPojo) {
        LOGGER.info("Contact pojo content: {}", contactPojo);
        emailService.sendEmail(contactPojo);
        return ContactController.CONTACT_US_VIEW_NAME;
    }
}
