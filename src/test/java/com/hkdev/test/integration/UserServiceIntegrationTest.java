package com.hkdev.test.integration;

import com.hkdev.backend.persistence.domain.backend.Role;
import com.hkdev.backend.persistence.domain.backend.User;
import com.hkdev.backend.persistence.domain.backend.UserRole;
import com.hkdev.backend.service.UserService;
import com.hkdev.enums.Plans;
import com.hkdev.enums.Roles;
import com.hkdev.utils.UserUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceIntegrationTest {

    @Autowired
    private UserService userService;

    @Test
    public void testCreateNewUser() throws Exception {
        Set<UserRole> userRoles = new HashSet<>();
        User basicUser = UserUtils.createBasicUser();
        userRoles.add(new UserRole(basicUser, new Role(Roles.BASIC)));

        User user = userService.createUser(basicUser, Plans.BASIC, userRoles);
        assertNotNull(user);
        assertNotNull(user.getId());
    }

}
