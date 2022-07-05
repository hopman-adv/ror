package com.rest.login.data;

import com.rest.login.operations.UserOperations;
import io.restassured.path.json.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.yaml.snakeyaml.events.Event;

@Component
public class UserSession {

    public static String USER_NAME;
    public static String USER_EMAIL;
    public static String USER_PASSWORD;

    public static String TOKEN;
    public static String USER_ID;
    public static String REFRESH_TOKEN;

    Logger log = LoggerFactory.getLogger(UserSession.class);

    public void setBasicTesterUser() {
        USER_NAME = "tester";
        USER_EMAIL = "tester@mail.com";
        USER_PASSWORD = "TesterHeslo@1";
    }

    public void saveUserSession(String username, String email, String password) {
        USER_NAME = username;
        USER_EMAIL = email;
        USER_PASSWORD = password;
    }

    public void saveUserSessionDataFromLoginRequest(JsonPath json) {
        TOKEN = json.getString("accessToken");
        USER_ID = json.getString("id");
        REFRESH_TOKEN = json.getString("refreshToken");
        userSessionIntoLogs();
    }

    public void userSessionIntoLogs() {
        log.info("User has parameters:");
        log.info("ID:"+ USER_ID);
        log.info("Username: "+USER_NAME);
        log.info("Email: "+USER_EMAIL);
        log.info("Password: "+USER_PASSWORD);
        log.info("Token: "+TOKEN);
        log.info("Refresh token: "+REFRESH_TOKEN);
    }
}
