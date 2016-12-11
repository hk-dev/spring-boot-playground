package com.hkdev.test.integration;

import com.hkdev.backend.persistence.domain.backend.User;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceIntegrationTest extends AbstractServiceIntegrationTest {

    @Rule
    public TestName testName = new TestName();

    @Test
    public void testCreateUser() throws Exception {
        User user = createUser(testName);
        assertNotNull(user);
        assertNotNull(user.getId());
    }

}
