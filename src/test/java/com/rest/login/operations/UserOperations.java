package com.rest.login.operations;

import com.rest.login.Application;
import com.rest.login.data.UserSession;
import com.rest.login.payload.request.LoginRequest;
import com.rest.login.payloads.TestUserPayload;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static com.rest.login.data.UserSession.TOKEN;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

@Component
public class UserOperations {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserSession userSession;

    Logger log = LoggerFactory.getLogger(UserOperations.class);

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

    public JsonPath loginRequest(LoginRequest loginRequest) {
        JsonPath json = given()
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(loginRequest)
                .when().
                post("https://localhost:8443/api/auth/signin").jsonPath();
        return json;
    }

    public void deleteUserByUsername(String username) {
        jdbcTemplate.execute("DELETE C FROM CLIENTS C JOIN USERS U ON U.ID=C.USER_ID WHERE U.USERNAME = '"+username+"';");
        jdbcTemplate.execute("DELETE RT FROM REFRESHTOKEN RT JOIN USERS U ON U.ID=RT.USER_ID WHERE U.USERNAME = '"+username+"';");
        jdbcTemplate.execute("DELETE UR FROM USER_ROLES UR JOIN USERS U ON U.ID=UR.USER_ID WHERE U.USERNAME = '"+username+"';");
        jdbcTemplate.execute("DELETE FROM USERS WHERE USERNAME = '"+username+"';");
    }

    public void deleteUserByUsernameAndClientId(String username, Long clientId) {
        jdbcTemplate.execute("DELETE E FROM CLIENTS C JOIN EVALUATION_RECORDS E ON C.ID = E.CLIENT_ID WHERE C.ID = '"+clientId+"';");
        jdbcTemplate.execute("DELETE FROM CLIENTS WHERE ID = '"+clientId+"';");
        jdbcTemplate.execute("DELETE RT FROM REFRESHTOKEN RT JOIN USERS U ON U.ID=RT.USER_ID WHERE U.USERNAME = '"+username+"';");
        jdbcTemplate.execute("DELETE UR FROM USER_ROLES UR JOIN USERS U ON U.ID=UR.USER_ID WHERE U.USERNAME = '"+username+"';");
        jdbcTemplate.execute("DELETE FROM USERS WHERE USERNAME = '"+username+"';");
    }

    public void deleteEvaluationsFromClient(Long clientId) {
        jdbcTemplate.execute("DELETE E FROM CLIENTS C JOIN EVALUATION_RECORDS E ON C.ID = E.CLIENT_ID WHERE C.ID = '"+clientId+"';");
    }

    public void deleteClientById(Long clientId) {
        jdbcTemplate.execute("DELETE FROM CLIENTS WHERE ID = '"+clientId+"';");
    }


    public void createTesterUser(String role, String email ,String username, String URL, String password) {
        Set<String> roles = new HashSet<String>();
        roles.add("user");
        TestUserPayload newUser = new TestUserPayload(username, email, roles, password);
        postRequest(newUser, "User registered successfully!", 200, URL);
    }

    public JsonPath getUserByUsername(String username, String token) {
        return given().header("Authorization", "Bearer "+TOKEN)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .when().
                get("https://localhost:8443/api/data/users/"+username).jsonPath();
    }

    public void getUserByUsernameSuccessCheck(String name) {
        JsonPath clientJson = getUserByUsername(name, TOKEN);

        String username = clientJson.getString("username");
        String email = clientJson.getString("email");
        String id = clientJson.getString("id");

        assertThat(username, is(UserSession.USER_NAME));
        assertThat(email, is(UserSession.USER_EMAIL));
        assertThat(id, is(UserSession.USER_ID));

        log.info("Returned client: NAME: "+username + " ID: "+id+ " EMAIL: "+email);
    }

    public JsonPath createAndLoginTesterUser(String url) {
        userSession.setBasicTesterUser();

        createTesterUser("user", UserSession.USER_EMAIL, UserSession.USER_NAME, url + "signup", UserSession.USER_PASSWORD);
        userSession.saveUserSession(userSession.USER_NAME, userSession.USER_EMAIL, userSession.USER_PASSWORD);
        JsonPath json = loginRequest(new LoginRequest(userSession.USER_NAME, userSession.USER_PASSWORD));
        userSession.saveUserSessionDataFromLoginRequest(json);
        return json;
    }

}
