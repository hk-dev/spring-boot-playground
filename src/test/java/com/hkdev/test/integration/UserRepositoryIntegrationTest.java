package com.hkdev.test.integration;

import com.hkdev.backend.persistence.domain.backend.Plan;
import com.hkdev.backend.persistence.domain.backend.Role;
import com.hkdev.backend.persistence.domain.backend.User;
import com.hkdev.backend.persistence.domain.backend.UserRole;
import com.hkdev.backend.persistence.repositories.PlanRepository;
import com.hkdev.backend.persistence.repositories.RoleRepository;
import com.hkdev.backend.persistence.repositories.UserRepository;
import com.hkdev.enums.Plans;
import com.hkdev.enums.Roles;
import com.hkdev.utils.UserUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserRepositoryIntegrationTest extends AbstractIntegrationTest {

    @Rule
    public TestName testName = new TestName();

    @Before
    public void init() {
        assertNotNull(planRepository);
        assertNotNull(roleRepository);
        assertNotNull(userRepository);
    }

    @Test
    public void testCreateNewPlan() throws Exception {
        Plan basicPlan = createPlan(Plans.BASIC);
        planRepository.save(basicPlan);
        Plan retrievedPlan = planRepository.findOne(Plans.BASIC.getId());
        assertNotNull(retrievedPlan);
    }

    @Test
    public void testCreateNewRole() throws Exception {
        Role basicRole = createRole(Roles.BASIC);
        roleRepository.save(basicRole);
        Role retrievedRole = roleRepository.findOne(Roles.BASIC.getId());
        assertNotNull(retrievedRole);
    }

    @Test
    public void testCreateNewUser() throws Exception {
        String username = testName.getMethodName();
        String email = testName.getMethodName() + "@mail.com";

        User basicUser = createUser(username, email);
        User newUser = userRepository.findOne(basicUser.getId());

        assertNotNull(newUser);
        assertTrue(newUser.getId() != 0);
        assertNotNull(newUser.getPlan());
        assertNotNull(newUser.getPlan().getId());

        Set<UserRole> newUserUserRoles = newUser.getUserRoles();
        newUserUserRoles.forEach(role -> {
            assertNotNull(role.getRole());
            assertNotNull(role.getRole().getId());
        });
    }

    @Test
    public void testDeleteUser() throws Exception {
        String username = testName.getMethodName();
        String email = testName.getMethodName() + "@mail.com";

        User basicUser = createUser(username, email);
        userRepository.delete(basicUser.getId());
    }

    @Test
    public void testFindUserByEmail() throws Exception {
        User user = createUser(testName);
        User newUser = userRepository.findByEmail(user.getEmail());
        assertNotNull(newUser);
    }

    @Test
    public void testUpdateUserPassword() throws Exception {
        User user = createUser(testName);
        assertNotNull(user);

        String newPassword = UUID.randomUUID().toString();
        userRepository.updateUserPassword(user.getId(), newPassword);
        user = userRepository.findOne(user.getId());
        assertEquals(newPassword, user.getPassword());
    }
}
