package com.rest.login;

import com.rest.login.operations.UserOperations;
import com.rest.login.payloads.TestUserPayload;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.HashSet;
import java.util.Set;


@SpringBootTest
public class UserSignupTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    static String BASE_URL = "https://localhost:8443/api/auth/signup";
    static String USER_NAME = "tester";

    @Autowired
    private UserOperations userOperations;

    @AfterEach
    void cleanupUsers() {
        userOperations.deleteUserByUsername(USER_NAME);
    }

    @Test
    void signupUserOK() {
        Set<String> roles = new HashSet<String>();
        roles.add("user");
        TestUserPayload newUser = new TestUserPayload(USER_NAME, "tester@email.com", roles, "TesterHeslo1@");
        userOperations.postRequest(newUser, "User registered successfully!", 200, BASE_URL);
    }

    @Test
    void signupExistingNameUser() {
        Set<String> roles = new HashSet<String>();
        roles.add("user");
        TestUserPayload newUser = new TestUserPayload(USER_NAME, "tester@email.com", roles, "TesterHeslo1@");
        TestUserPayload sameNameUser = new TestUserPayload("tester", "t@email.com", roles, "TestHeslo1@");

        userOperations.postRequest(newUser, "User registered successfully!", 200, BASE_URL);
        userOperations.postRequest(sameNameUser, "Error: Username is already taken!", 400, BASE_URL);
    }

    @Test
    void signupExistingMailUser() {
        Set<String> roles = new HashSet<>();
        roles.add("user");
        TestUserPayload newUser = new TestUserPayload(USER_NAME, "tester@email.com", roles, "TesterHeslo1@");
        TestUserPayload sameMailUser = new TestUserPayload("test", "tester@email.com", roles, "TestHeslo1@");

        userOperations.postRequest(newUser, "User registered successfully!", 200, BASE_URL);
        userOperations.postRequest(sameMailUser, "Error: Email is already in use!", 400, BASE_URL);
    }

}
