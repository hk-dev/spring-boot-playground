package com.hkdev.test.integration;

import com.hkdev.backend.persistence.domain.backend.*;
import com.hkdev.backend.persistence.repositories.PasswordResetTokenRepository;
import com.hkdev.backend.persistence.repositories.PlanRepository;
import com.hkdev.backend.persistence.repositories.RoleRepository;
import com.hkdev.backend.persistence.repositories.UserRepository;
import com.hkdev.enums.Plans;
import com.hkdev.enums.Roles;
import com.hkdev.utils.UserUtils;
import org.junit.rules.TestName;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractIntegrationTest {

    @Autowired
    protected PlanRepository planRepository;

    @Autowired
    protected RoleRepository roleRepository;

    @Autowired
    protected UserRepository userRepository;

    @Autowired
    protected PasswordResetTokenRepository passwordResetTokenRepository;

    @Value("${token.expiration.length.minutes}")
    protected int expirationTimeInMinutes;

    protected Plan createPlan(Plans plans) {
        return new Plan(plans);
    }

    protected Role createRole(Roles roles) {
        return new Role(roles);
    }

    protected User createUser(String username, String email) {
        Plan basicPlan = createPlan(Plans.BASIC);
        planRepository.save(basicPlan);

        User basicUser = UserUtils.createBasicUser(username, email);
        basicUser.setPlan(basicPlan);

        Role basicRole = createRole(Roles.BASIC);
        roleRepository.save(basicRole);

        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole(basicUser, basicRole);
        userRoles.add(userRole);

        basicUser.getUserRoles().addAll(userRoles);
        basicUser = userRepository.save(basicUser);
        return basicUser;
    }

    protected User createUser(TestName testName) {
        return createUser(testName.getMethodName(), testName.getMethodName() + "@mail.com");
    }

    protected PasswordResetToken createPasswordResetToken(String token, User user, LocalDateTime localDateTime) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, localDateTime, expirationTimeInMinutes);
        passwordResetTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }
}
