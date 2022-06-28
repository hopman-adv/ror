package com.rest.login.operations;

import com.rest.login.payloads.TestUserPayload;
import io.restassured.http.ContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

@Component
public class UserOperations {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void postRequest(TestUserPayload payload, String message, int statusCode, String URL) {
        given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(payload)
                .when().
                post(URL).
                then().
                statusCode(statusCode).
                body("message", equalTo(message));
    }

    public void deleteUserByUsername(String username) {
        jdbcTemplate.execute("DELETE UR FROM USER_ROLES UR JOIN USERS U ON U.ID=UR.USER_ID WHERE U.USERNAME = '"+username+"';");
        jdbcTemplate.execute("DELETE FROM USERS WHERE USERNAME = '"+username+"';");

    }
}
