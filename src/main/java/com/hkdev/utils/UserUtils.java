package com.hkdev.utils;

import com.hkdev.backend.persistence.domain.backend.User;

public class UserUtils {

    private UserUtils() {
    }

    public static User createBasicUser() {
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
