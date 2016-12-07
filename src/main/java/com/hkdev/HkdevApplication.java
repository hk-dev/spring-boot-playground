package com.hkdev;

import com.hkdev.backend.persistence.domain.backend.Role;
import com.hkdev.backend.persistence.domain.backend.User;
import com.hkdev.backend.persistence.domain.backend.UserRole;
import com.hkdev.backend.service.UserService;
import com.hkdev.enums.Plans;
import com.hkdev.enums.Roles;
import com.hkdev.utils.UserUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class HkdevApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(HkdevApplication.class);

	@Autowired
	private UserService userService;

	public static void main(String[] args) {
		SpringApplication.run(HkdevApplication.class, args);
	}

	@Override
	public void run(String... args) {
		String username = "proUser";
		String email = "proUser@mail.com";

		User user = UserUtils.createBasicUser(username, email);
		Set<UserRole> userRoles = new HashSet<>();
		userRoles.add(new UserRole(user, new Role(Roles.BASIC)));
		LOGGER.debug("Creating user with username {}", user.getUsername());
		userService.createUser(user, Plans.PRO, userRoles);
		LOGGER.info("User {} created", user.getUsername());
	}
}
