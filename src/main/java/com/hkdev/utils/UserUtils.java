package com.hkdev.utils;

import com.hkdev.backend.persistence.domain.backend.User;

public class UserUtils {

    private UserUtils() {
    }

    public static User createBasicUser(String username, String email) {
        User user = new User();
        user.setUsername(username);
        user.setPassword("password");
        user.setEmail(email);
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
