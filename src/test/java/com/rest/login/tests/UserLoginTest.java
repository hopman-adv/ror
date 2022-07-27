package com.rest.login.tests;

import com.rest.login.data.UserSession;
import com.rest.login.operations.UserOperations;
import groovy.util.logging.Slf4j;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@Slf4j
@SpringBootTest
public class UserLoginTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    static String AUTH_URL = "https://localhost:8443/api/auth/";

    @Autowired
    private UserSession userSession;

    @Autowired
    private UserOperations userOperations;

    @AfterEach
    void cleanupUsers() {
        userOperations.deleteUserByUsername(userSession.USER_NAME);
    }

    @Test
    void loginUser() {
        JsonPath json = userOperations.createAndLoginTesterUser(AUTH_URL);
        userOperations.getUserByUsernameSuccessCheck(userSession.USER_NAME);
        userSession.userSessionIntoLogs();
    }

}
