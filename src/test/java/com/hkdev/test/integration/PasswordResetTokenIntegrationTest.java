package com.hkdev.test.integration;

import com.hkdev.backend.persistence.domain.backend.PasswordResetToken;
import com.hkdev.backend.persistence.domain.backend.User;
import com.hkdev.backend.persistence.repositories.PasswordResetTokenRepository;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class PasswordResetTokenIntegrationTest extends AbstractIntegrationTest {

    @Value("${token.expiration.length.minutes}")
    private int expirationTimeInMinutes;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Rule
    public TestName testName = new TestName();

    @Before
    public void init() throws Exception {
        assertFalse(expirationTimeInMinutes == 0);
    }

    @Test
    public void testTokenExpirationLength() throws Exception {
        User user = createUser(testName);
        assertNotNull(user);
        assertNotNull(user.getId());

        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        String token = UUID.randomUUID().toString();

        LocalDateTime expectedTime = now.plusMinutes(expirationTimeInMinutes);
        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);

        LocalDateTime actualTime = passwordResetToken.getExpiryDate();
        assertNotNull(actualTime);
        assertEquals(expectedTime, actualTime);
    }

    @Test
    public void testFindTokenByTokenValue() throws Exception {
        User user = createUser(testName);

        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        String token = UUID.randomUUID().toString();

        createPasswordResetToken(token, user, now);

        PasswordResetToken retrivedToken = passwordResetTokenRepository.findByToken(token);

        assertNotNull(retrivedToken);
        assertNotNull(retrivedToken.getId());
        assertNotNull(retrivedToken.getUser());
    }

    @Test
    public void testDeleteToken() throws Exception {
        User user = createUser(testName);

        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        String token = UUID.randomUUID().toString();
        PasswordResetToken passwordResetToken = createPasswordResetToken(token, user, now);
        long tokenId = passwordResetToken.getId();
        passwordResetTokenRepository.delete(tokenId);

        PasswordResetToken deletedToken = passwordResetTokenRepository.findOne(tokenId);
        assertNull(deletedToken);
    }

    @Test
    public void testCascadeDeleteFromUserEntity() throws Exception {
        User user = createUser(testName);

        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        String token = UUID.randomUUID().toString();
        createPasswordResetToken(token, user, now);
        userRepository.delete(user.getId());

        Set<PasswordResetToken> deletedToken = passwordResetTokenRepository.findAllByUserId(user.getId());
        assertTrue(deletedToken.isEmpty());
    }

    @Test
    public void testMultipleTokensAreReturnedWhenQueryingByUserId() throws Exception {
        User user = createUser(testName);

        LocalDateTime now = LocalDateTime.now(Clock.systemUTC());
        String token1 = UUID.randomUUID().toString();
        String token2 = UUID.randomUUID().toString();
        String token3 = UUID.randomUUID().toString();

        Set<PasswordResetToken> tokens = new HashSet<>();
        tokens.add(createPasswordResetToken(token1, user, now));
        tokens.add(createPasswordResetToken(token2, user, now));
        tokens.add(createPasswordResetToken(token3, user, now));

        passwordResetTokenRepository.save(tokens);

        User foundUser = userRepository.findOne(user.getId());

        Set<PasswordResetToken> actualTokens = passwordResetTokenRepository.findAllByUserId(foundUser.getId());
        assertTrue(actualTokens.size() == tokens.size());

        List<String> tokensList = tokens.stream().map(PasswordResetToken::getToken).collect(Collectors.toList());
        List<String> actualTokenList = actualTokens.stream().map(PasswordResetToken::getToken).collect(Collectors.toList());
        assertEquals(tokensList, actualTokenList);
    }

    private PasswordResetToken createPasswordResetToken(String token, User user, LocalDateTime localDateTime) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user, localDateTime, expirationTimeInMinutes);
        passwordResetTokenRepository.save(passwordResetToken);
        return passwordResetToken;
    }
}
