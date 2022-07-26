package com.rest.login.operations;

import com.rest.login.Application;
import com.rest.login.data.UserSession;
import com.rest.login.payload.request.LoginRequest;
import com.rest.login.payload.request.UpdateRequest;
import com.rest.login.payloads.TestUserPayload;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static com.rest.login.data.UserSession.*;
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
        jdbcTemplate.execute("DELETE C FROM CLIENTS C JOIN USERS U ON U.ID=C.USER_ID WHERE U.USERNAME = '" + username + "';");
        jdbcTemplate.execute("DELETE RT FROM REFRESHTOKEN RT JOIN USERS U ON U.ID=RT.USER_ID WHERE U.USERNAME = '" + username + "';");
        jdbcTemplate.execute("DELETE UR FROM USER_ROLES UR JOIN USERS U ON U.ID=UR.USER_ID WHERE U.USERNAME = '" + username + "';");
        jdbcTemplate.execute("DELETE FROM USERS WHERE USERNAME = '" + username + "';");
    }

    public void deleteUserByUsernameAndClientId(String username, Long clientId) {
        jdbcTemplate.execute("DELETE FROM ANSWERS");
        jdbcTemplate.execute("DELETE FROM BOARDS");
        jdbcTemplate.execute("DELETE E FROM CLIENTS C JOIN EVALUATION_RECORDS E ON C.ID = E.CLIENT_ID WHERE C.ID = '" + clientId + "';");
        jdbcTemplate.execute("DELETE FROM CLIENTS WHERE ID = '" + clientId + "';");
        jdbcTemplate.execute("DELETE RT FROM REFRESHTOKEN RT JOIN USERS U ON U.ID=RT.USER_ID WHERE U.USERNAME = '" + username + "';");
        jdbcTemplate.execute("DELETE UR FROM USER_ROLES UR JOIN USERS U ON U.ID=UR.USER_ID WHERE U.USERNAME = '" + username + "';");
        jdbcTemplate.execute("DELETE FROM USERS WHERE USERNAME = '" + username + "';");
    }

    public void deleteEvaluationsFromClient(Long clientId) {
        jdbcTemplate.execute("DELETE E FROM CLIENTS C JOIN EVALUATION_RECORDS E ON C.ID = E.CLIENT_ID WHERE C.ID = '" + clientId + "';");
    }

    public void deleteClientById(Long clientId) {
        jdbcTemplate.execute("DELETE FROM CLIENTS WHERE ID = '" + clientId + "';");
    }

    public void deleteAllBoards() {
        jdbcTemplate.execute("DELETE FROM ANSWERS");
        jdbcTemplate.execute("DELETE FROM BOARDS");
    }

    public void deleteAll() {
        jdbcTemplate.execute("DELETE FROM ANSWERS");
        jdbcTemplate.execute("DELETE FROM BOARDS");
        jdbcTemplate.execute("DELETE FROM evaluation_records");
        jdbcTemplate.execute("DELETE FROM CLIENTS");
        jdbcTemplate.execute("DELETE FROM USER_ROLES");
        jdbcTemplate.execute("DELETE FROM refreshtoken");
        jdbcTemplate.execute("DELETE FROM USERS");
    }

    public void createTesterUser(String role, String email, String username, String URL, String password) {
        Set<String> roles = new HashSet<String>();
        roles.add("user");
        TestUserPayload newUser = new TestUserPayload(username, email, roles, password);
        postRequest(newUser, "User registered successfully!", 200, URL);
    }

    public Response getUserByUsername(String username) {
        return given().header("Authorization", "Bearer " + TOKEN)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .when().
                get("https://localhost:8443/api/data/users/" + username);
    }

    public void getUserByUsernameSuccessCheck(String name) {
        JsonPath clientJson = getUserByUsername(name).jsonPath();
        log.info(clientJson.prettify());
        String prefix = "userDetails.";
        String username = clientJson.getString(prefix + "username");
        String email = clientJson.getString(prefix + "email");
        String id = clientJson.getString(prefix + "id");

        assertThat(username, is(UserSession.USER_NAME));
        assertThat(email, is(UserSession.USER_EMAIL));
        assertThat(id, is(UserSession.USER_ID));

        log.info("Returned client: NAME: " + username + " ID: " + id + " EMAIL: " + email);
    }

    public JsonPath createAndLoginTesterUser(String url) {
        userSession.setBasicTesterUser();

        createTesterUser("user", UserSession.USER_EMAIL, UserSession.USER_NAME, url + "signup", UserSession.USER_PASSWORD);
        userSession.saveUserSession(userSession.USER_NAME, userSession.USER_EMAIL, userSession.USER_PASSWORD);
        JsonPath json = loginRequest(new LoginRequest(userSession.USER_NAME, userSession.USER_PASSWORD));
        userSession.saveUserSessionDataFromLoginRequest(json);
        return json;
    }

    public JsonPath createAndLoginSecondTesterUser(String url) {
        userSession.setSecondTesterUser();

        createTesterUser("user", UserSession.USER2_EMAIL, UserSession.USER2_NAME, url + "signup", UserSession.USER2_PASSWORD);
        userSession.saveSecondUserSession(userSession.USER2_NAME, userSession.USER2_EMAIL, userSession.USER2_PASSWORD);
        JsonPath json = loginRequest(new LoginRequest(userSession.USER2_NAME, userSession.USER2_PASSWORD));
        userSession.saveSecondUserSessionDataFromLoginRequest(json);
        return json;
    }

    public Response editUser(UpdateRequest updateRequest, String userId, String token) {
        return given().header("Authorization", "Bearer " + token)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(updateRequest)
                .when().
                put("https://localhost:8443/api/data/users/" + userId);
    }

    public JsonPath updatePrecreatedUser(String name, String email) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setUsername(name);
        updateRequest.setEmail(email);
        return editUser(updateRequest, USER_ID, TOKEN).jsonPath();
    }

    public Response tryToUpdateSecondPrecreatedUserByFirstUser(String name, String email) {
        UpdateRequest updateRequest = new UpdateRequest();
        updateRequest.setUsername(name);
        updateRequest.setEmail(email);
        String firstUserToken = TOKEN;
        String secondUserId = USER2_ID;
        return editUser(updateRequest, secondUserId, firstUserToken);
    }


}
