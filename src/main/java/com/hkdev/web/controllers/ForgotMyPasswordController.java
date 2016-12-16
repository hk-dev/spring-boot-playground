package com.hkdev.web.controllers;

import com.hkdev.backend.persistence.domain.backend.PasswordResetToken;
import com.hkdev.backend.persistence.domain.backend.User;
import com.hkdev.backend.service.EmailService;
import com.hkdev.backend.service.I18NService;
import com.hkdev.backend.service.PasswordResetTokenService;
import com.hkdev.backend.service.UserService;
import com.hkdev.utils.UserUtils;
import org.codehaus.groovy.runtime.metaclass.ClosureMetaClass;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.time.Clock;
import java.time.LocalDateTime;
import java.util.Locale;

@Controller
public class ForgotMyPasswordController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForgotMyPasswordController.class);

    public static final String EMAIL_ADDRESS_VIEW_NAME = "forgotmypassword/emailForm";
    public static final String FORGOT_MY_PASSWORD_MAPPING = "/forgotmypassword";
    public static final String MAIL_SENT_KEY = "mailSent";
    public static final String CHANGE_PASSWORD_PATH = "/changeuserpassword";
    public static final String EMAIL_MESSAGE_TEXT_PROPERTY_NAME = "forgotmypassword.email.text";
    public static final String CHANGE_PASSWORD_VIEW_NAME = "/forgotmypassword/changePassword";
    private static final String PASSWORD_RESET_ATTRIBUTE_NAME = "passwordReset";
    private static final String MESSAGE_ATTRIBUTE_NAME = "message";

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private I18NService i18NService;

    @Value("${webmaster.email}")
    private String webmasterEmail;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserService userService;

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

    @RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.GET)
    public String changeUserPasswordGet(@RequestParam("id") long id, @RequestParam("token") String token, Locale locale, ModelMap modelMap) {
        if(StringUtils.isEmpty(token) || id == 0) {
            LOGGER.error("Invalid user id {} or token value {}", id, token);
            modelMap.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            modelMap.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Invalid user id or token value");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        PasswordResetToken passwordResetToken = passwordResetTokenService.findByToken(token);

        if(passwordResetToken == null) {
            LOGGER.warn("A token couldn't be found with value {}", token);
            modelMap.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            modelMap.addAttribute(MESSAGE_ATTRIBUTE_NAME, "Token not found");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        User user = passwordResetToken.getUser();

        if(user.getId() != id) {
            LOGGER.error("The user id {} doesn't match the userd id {} with the token {}", id, user.getId(), token);
            modelMap.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            modelMap.addAttribute(MESSAGE_ATTRIBUTE_NAME, i18NService.getMessage("resetPassword.token.invalid", locale));
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        if(LocalDateTime.now(Clock.systemUTC()).isAfter(passwordResetToken.getExpiryDate())) {
            LOGGER.error("The token {} has expired", token);
            modelMap.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            modelMap.addAttribute(MESSAGE_ATTRIBUTE_NAME, i18NService.getMessage("resetPassword.token.expired", locale));
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        modelMap.addAttribute("principalId", user.getId());

        Authentication authentication = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return CHANGE_PASSWORD_VIEW_NAME;
    }

    @RequestMapping(value = CHANGE_PASSWORD_PATH, method = RequestMethod.POST)
    public String changeUserPasswordPost(@RequestParam("principal_id") long userId, @RequestParam("password") String password, ModelMap modelMap) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if(authentication == null) {
            LOGGER.error("An unauthenticated user tried to invoke the reset password POST method");
            modelMap.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            modelMap.addAttribute(MESSAGE_ATTRIBUTE_NAME, "You are not authorized to perform this request.");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        User user = (User) authentication.getPrincipal();

        if(user.getId() != userId) {
            LOGGER.error("Security breach! User {} is trying to make a password reset request on behalf of {}", user.getId(), userId);
            modelMap.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "false");
            modelMap.addAttribute(MESSAGE_ATTRIBUTE_NAME, "You are not authorized to perform this request.");
            return CHANGE_PASSWORD_VIEW_NAME;
        }

        userService.updateUserPassword(userId, password);
        LOGGER.info("Password successfully updated for user {}", user.getUsername());
        modelMap.addAttribute(PASSWORD_RESET_ATTRIBUTE_NAME, "true");
        return CHANGE_PASSWORD_VIEW_NAME;
    }
}
