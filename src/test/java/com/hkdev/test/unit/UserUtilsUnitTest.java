package com.hkdev.test.unit;

import com.hkdev.utils.UserUtils;
import com.hkdev.web.controllers.ForgotMyPasswordController;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpServletRequest;

import java.util.UUID;

import static org.junit.Assert.assertEquals;

public class UserUtilsUnitTest {

    private MockHttpServletRequest mockHttpServletRequest;

    @Before
    public void init() throws Exception {
        mockHttpServletRequest = new MockHttpServletRequest();
    }

    @Test
    public void testPasswordResetEmailUrlConstruction() throws Exception {
        mockHttpServletRequest.setServerPort(8080);

        String token = UUID.randomUUID().toString();
        long userdId = 123456;

        String expectedUrl = "http://localhost:8080" + ForgotMyPasswordController.CHANGE_PASSWORD_PATH + "?id=" + userdId + "&token=" + token;
        String actualUrl = UserUtils.createPasswordResetUrl(mockHttpServletRequest, userdId, token);

        assertEquals(expectedUrl, actualUrl);
    }
}
