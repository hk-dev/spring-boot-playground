package com.hkdev.backend.service;

import com.hkdev.backend.persistence.domain.backend.Plan;
import com.hkdev.backend.persistence.domain.backend.User;
import com.hkdev.backend.persistence.domain.backend.UserRole;
import com.hkdev.backend.persistence.repositories.PlanRepository;
import com.hkdev.backend.persistence.repositories.RoleRepository;
import com.hkdev.backend.persistence.repositories.UserRepository;
import com.hkdev.enums.Plans;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
@Transactional(readOnly = true)
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PlanRepository planRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Transactional
    public User createUser(User user, Plans plans, Set<UserRole> userRoles) {
        Plan plan = new Plan(plans);

        if(!planRepository.exists(plans.getId())) {
            plan = planRepository.save(plan);
        }

        user.setPlan(plan);
        userRoles.forEach(userRole -> roleRepository.save(userRole.getRole()));
        user.getUserRoles().addAll(userRoles);
        user = userRepository.save(user);
        return user;
    }
}
