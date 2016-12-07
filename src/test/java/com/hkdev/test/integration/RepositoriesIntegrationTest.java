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
        Plan plan = createPlan(Plans.BASIC);
        planRepository.save(plan);

        User basicUser = UserUtils.createBasicUser();
        basicUser.setPlan(plan);

        Role basicRole = createRole(Roles.BASIC);

        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole(basicUser, basicRole);
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

    private Plan createPlan(Plans plans) {
        return new Plan(plans);
    }

    private Role createRole(Roles roles) {
        return new Role(roles);
    }
}
