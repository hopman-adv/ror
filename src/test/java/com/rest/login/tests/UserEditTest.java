package com.rest.login.tests;

import com.rest.login.data.UserSession;
import com.rest.login.operations.ClientOperations;
import com.rest.login.operations.UserOperations;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static com.rest.login.data.UserSession.TOKEN2;
import static com.rest.login.data.UserSession.USER2_ID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


@SpringBootTest
public class UserEditTest {

    Logger log = LoggerFactory.getLogger(UserEditTest.class);

    @Autowired
    private JdbcTemplate jdbcTemplate;

    static String AUTH_URL = "https://localhost:8443/api/auth/";
    static String BASE_URL = "https://localhost:8443/api/auth/signup";
    static String USER_NAME = "tester";

    JsonPath client;
    JsonPath client2;

    @Autowired
    private UserOperations userOperations;

    @Autowired
    private UserSession userSession;

    @Autowired
    ClientOperations clientOperations;

    @BeforeEach
    void createUser() {
        userOperations.createAndLoginTesterUser(AUTH_URL);
        userOperations.createAndLoginSecondTesterUser(AUTH_URL);

        client = clientOperations.createAndReturnRandomNameClient();
        client2 = clientOperations.createAndReturnRandomNameClient(USER2_ID, TOKEN2);
    }

    @AfterEach
    void cleanupUsers() {
        userOperations.deleteAll();
    }

    @Test
    void userCanEditHisOwnInfo() {
        userOperations.getUserByUsernameSuccessCheck(userSession.USER_NAME);
        String email = "lojza@email.com";
        String name = "Lojza";
        JsonPath json = userOperations.updatePrecreatedUser(name, email);
        log.info(json.prettify());
        assertThat(json.getString("message"), equalTo("User successfully updated!"));
        assertThat(json.getString("userDetails.username"), equalTo(name));
        assertThat(json.getString("userDetails.email"), equalTo(email));
    }

    @Test
    void userCannotEditOtherUserInfo() {
        userOperations.getUserByUsernameSuccessCheck(userSession.USER_NAME);
        String email = "lojza@email.com";
        String name = "Lojza";

        Response response = userOperations.tryToUpdateSecondPrecreatedUserByFirstUser(name, email);
        assertThat(response.statusCode(), equalTo(403));
    }

}
