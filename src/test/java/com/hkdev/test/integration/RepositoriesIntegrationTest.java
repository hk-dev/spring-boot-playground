package com.hkdev.test.integration;

import com.hkdev.backend.persistence.domain.backend.Plan;
import com.hkdev.backend.persistence.domain.backend.Role;
import com.hkdev.backend.persistence.domain.backend.User;
import com.hkdev.backend.persistence.domain.backend.UserRole;
import com.hkdev.backend.persistence.repositories.PlanRepository;
import com.hkdev.backend.persistence.repositories.RoleRepository;
import com.hkdev.backend.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class RepositoriesIntegrationTest {

    @Autowired
    private PlanRepository planRepository;
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int BASIC_PLAN_ID = 1;
    private static final int BASIC_ROLE_ID = 1;

    @Before
    public void init() {
        assertNotNull(planRepository);
        assertNotNull(roleRepository);
        assertNotNull(userRepository);
    }

    @Test
    public void testCreateNewPlan() throws Exception {
        Plan basicPlan = createBasicPlan();
        planRepository.save(basicPlan);
        Plan retrievedPlan = planRepository.findOne(BASIC_PLAN_ID);
        assertNotNull(retrievedPlan);
    }

    @Test
    public void testCreateNewRole() throws Exception {
        Role basicRole = createBasicRole();
        roleRepository.save(basicRole);
        Role retrievedRole = roleRepository.findOne(BASIC_ROLE_ID);
        assertNotNull(retrievedRole);
    }

    @Test
    public void testCreateNewUser() throws Exception {
        Plan plan = createBasicPlan();
        planRepository.save(plan);

        User basicUser = createBasicUser();
        basicUser.setPlan(plan);

        Role basicRole = createBasicRole();

        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole();
        userRole.setUser(basicUser);
        userRole.setRole(basicRole);
        userRoles.add(userRole);

        basicUser.getUserRoles().addAll(userRoles);

        userRoles.forEach(role -> roleRepository.save(role.getRole()));

        basicUser = userRepository.save(basicUser);
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

    private Plan createBasicPlan() {
        Plan plan = new Plan();
        plan.setId(BASIC_PLAN_ID);
        plan.setName("Basic");
        return plan;
    }

    private Role createBasicRole() {
        Role role = new Role();
        role.setId(BASIC_ROLE_ID);
        role.setName("Basic");
        return role;
    }

    private User createBasicUser() {
        User user = new User();
        user.setUsername("basicUser");
        user.setPassword("password");
        user.setEmail("basicUser@mail.com");
        user.setFirstName("John");
        user.setLastName("Doe");
        user.setPhoneNumber("123456789");
        user.setCountry("GB");
        user.setEnabled(true);
        user.setDescription("A user");
        user.setProfileImageUrl("http://test.images.com/basicuser123");
        return user;
    }
}
