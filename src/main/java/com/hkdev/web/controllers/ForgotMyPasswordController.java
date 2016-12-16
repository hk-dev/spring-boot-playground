package com.hkdev.web.controllers;

import com.hkdev.backend.persistence.domain.backend.PasswordResetToken;
import com.hkdev.backend.persistence.domain.backend.User;
import com.hkdev.backend.service.EmailService;
import com.hkdev.backend.service.PasswordResetTokenService;
import com.hkdev.utils.UserUtils;
import com.hkdev.backend.service.I18NService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sun.java2d.pipe.SpanShapeRenderer;

import javax.servlet.http.HttpServletRequest;

@Controller
public class ForgotMyPasswordController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotMyPasswordController.class);

    public static final String EMAIL_ADDRESS_VIEW_NAME = "forgotmypassword/emailForm";
    public static final String FORGOT_MY_PASSWORD_MAPPING = "/forgotmypassword";
    public static final String MAIL_SENT_KEY = "mailSent";
    public static final String CHANGE_PASSWORD_PATH = "/changeuserpassword";
    public static final String EMAIL_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private I18NService i18NService;

    @Value("${webmaster.email}")
    private String webmasterEmail;

    @Autowired
    private EmailService emailService;


    @RequestMapping(value = FORGOT_MY_PASSWORD_MAPPING, method = RequestMethod.GET)
    public String forgotMyPasswordGet() {
        return EMAIL_ADDRESS_VIEW_NAME;
    }

    @RequestMapping(value = FORGOT_MY_PASSWORD_MAPPING, method = RequestMethod.POST)
    public String forgotPasswordPost(HttpServletRequest request, @RequestParam("email") String email, ModelMap modelMap) {

        PasswordResetToken resetToken = passwordResetTokenService.createPasswordResetTokenForEmail(email);

        if(resetToken == null) {
            LOGGER.warn("Couldn't find a password reset token for email {}", email);
        } else {
            User user = resetToken.getUser();
            String token = resetToken.getToken();

            String passwordResetUrl = UserUtils.createPasswordResetUrl(request, user.getId(), token);
            String emailText = i18NService.getMessage(EMAIL_MESSAGE_TEXT_PROPERTY_NAME, request.getLocale());
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(user.getEmail());
            mailMessage.setSubject("[HK-Dev] Reset Your Password");
            mailMessage.setText(emailText + "\r\n" + passwordResetUrl);
            mailMessage.setFrom(webmasterEmail);
            emailService.sendGenericEmailMessage(mailMessage);
        }

        modelMap.addAttribute(MAIL_SENT_KEY, "true");

        return EMAIL_ADDRESS_VIEW_NAME;
    }
}
