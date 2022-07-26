package com.rest.login;

import com.rest.login.data.UserSession;
import com.rest.login.operations.ClientOperations;
import com.rest.login.operations.UserOperations;
import com.rest.login.payload.request.AddClientRequest;
import groovy.util.logging.Slf4j;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.*;
import java.util.stream.Collectors;

import static com.rest.login.UserSignupTest.USER_NAME;
import static com.rest.login.enums.EResponses.CLIENT_NOT_FOUND;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.Is.is;

@Slf4j
@SpringBootTest
public class ClientTest {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    static String AUTH_URL = "https://localhost:8443/api/auth/";
    static String BASE_URL = "https://localhost:8443/api/data/";
    static Long NON_EXISTING_CLIENT_ID = 50L;

    @Autowired
    private UserSession userSession;

    @Autowired
    private UserOperations userOperations;

    @Autowired
    private ClientOperations clientOperations;

    Logger log = LoggerFactory.getLogger(ClientTest.class);

    @BeforeEach
    void createUser() {
        userOperations.createAndLoginTesterUser(AUTH_URL);
    }

    @AfterEach
    void cleanupUsers() {
        userOperations.deleteUserByUsername(USER_NAME);
    }

    @Test
    void createOnlyNameClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameClient();
        log.info(client.prettify());

        assertThat(client.getString("name"), not(emptyOrNullString()));
        assertThat(client.getString("id"), not(emptyOrNullString()));
        assertThat(client.getString("email"), emptyOrNullString());
        assertThat(client.getString("description"), emptyOrNullString());

        JsonPath json = clientOperations.checkClientByUserIdClientId(client.getLong("id"));
        log.info(json.prettify());
    }

    @Test
    void createNameEmailClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameEmailClient();
        log.info(client.prettify());

        assertThat(client.getString("name"), not(emptyOrNullString()));
        assertThat(client.getString("id"), not(emptyOrNullString()));
        assertThat(client.getString("email"), not(emptyOrNullString()));
        assertThat(client.getString("description"), emptyOrNullString());

        JsonPath json = clientOperations.checkClientByUserIdClientId(client.getLong("id"));
        log.info(json.prettify());
    }

    @Test
    void createFullClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameEmailDescriptionClient();
        log.info(client.prettify());

        assertThat(client.getString("name"), not(emptyOrNullString()));
        assertThat(client.getString("id"), not(emptyOrNullString()));
        assertThat(client.getString("email"), not(emptyOrNullString()));
        assertThat(client.getString("description"), not(emptyOrNullString()));

        JsonPath json = clientOperations.checkClientByUserIdClientId(client.getLong("id"));
        log.info(json.prettify());
    }

    @Test
    void createNameDescriptionClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameDescriptionClient();
        log.info(client.prettify());

        assertThat(client.getString("name"), not(emptyOrNullString()));
        assertThat(client.getString("id"), not(emptyOrNullString()));
        assertThat(client.getString("email"), emptyOrNullString());
        assertThat(client.getString("description"), not(emptyOrNullString()));

        JsonPath json = clientOperations.checkClientByUserIdClientId(client.getLong("id"));
        log.info(json.prettify());
    }

    @Test
    void createMissingNameClient() {
        JsonPath client = clientOperations.createAndReturnMissingNameClient();

        assertThat(client.getString("status"), equalTo("400"));
        assertThat(client.getString("message"), equalTo("Validation failed for object='addClientRequest'. Error count: 1"));
    }

    @Test
    void createNameWithDiacriticsClient() {
        JsonPath client = clientOperations.createAndReturnSpecificNameClient("češký muž");
        log.info(client.prettify());

        assertThat(client.getString("name"), not(emptyOrNullString()));
        assertThat(client.getString("id"), not(emptyOrNullString()));
        assertThat(client.getString("email"), not(emptyOrNullString()));
        assertThat(client.getString("description"), not(emptyOrNullString()));

        JsonPath json = clientOperations.checkClientByUserIdClientId(client.getLong("id"));
        log.info(json.prettify());
    }

    @Test
    void createWrongNameClient() {
        JsonPath client = clientOperations.createAndReturnSpecificNameClient("/Test/");
        log.info(client.prettify());
        assertThat(client.getString("status"), equalTo("500"));
        assertThat(client.getString("message"), stringContainsInOrder("Validation failed for classes"));


        client = clientOperations.createAndReturnSpecificNameClient("#pepa");
        log.info(client.prettify());
        assertThat(client.getString("status"), equalTo("500"));
        assertThat(client.getString("message"), stringContainsInOrder("Validation failed for classes"));

        client = clientOperations.createAndReturnSpecificNameClient("123456789012345678901");
        log.info(client.prettify());
        assertThat(client.getString("status"), equalTo("500"));
        assertThat(client.getString("message"), stringContainsInOrder("Validation failed for classes"));
    }

    @Test
    void createSameNameClient() {
        JsonPath client = clientOperations.createAndReturnSpecificNameClient("test-client");
        log.info(client.prettify());

        assertThat(client.getString("name"), not(emptyOrNullString()));
        assertThat(client.getString("id"), not(emptyOrNullString()));
        assertThat(client.getString("email"), not(emptyOrNullString()));
        assertThat(client.getString("description"), not(emptyOrNullString()));
        Long firstId = client.getLong("id");

        client = clientOperations.createAndReturnSpecificNameClient("test-client");

        assertThat(client.getLong("id") - 1, equalTo(firstId));
        assertThat(client.getString("name"), not(emptyOrNullString()));
        assertThat(client.getString("id"), not(emptyOrNullString()));
        assertThat(client.getString("email"), not(emptyOrNullString()));
        assertThat(client.getString("description"), not(emptyOrNullString()));

        JsonPath json = clientOperations.checkClientByUserIdClientId(client.getLong("id"));
        log.info(json.prettify());
    }

    @Test
    void deleteClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameEmailDescriptionClient();
        log.info(client.prettify());

        JsonPath response = clientOperations.deleteClientById(client.getLong("id"));
        log.info(response.prettify());

        assertThat(response.getString("message"), equalTo("Client deleted!"));

        JsonPath json = clientOperations.checkClientByUserIdClientId(client.getLong("id"));
        assertThat(json.getString("status"), equalTo("500"));
        assertThat(json.getString("message"), equalTo("Error: Client not found in database!"));
    }

    @Test
    void deleteNonExistingClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameEmailDescriptionClient();
        log.info(client.prettify());

        JsonPath response = clientOperations.deleteClientById(NON_EXISTING_CLIENT_ID);

        assertThat(response.getString("message"), equalTo(CLIENT_NOT_FOUND.getMessage()));
    }

    @Test
    void editClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameEmailDescriptionClient();
        log.info(client.prettify());

        JsonPath json = clientOperations.editAllAttributesClientById(client.getLong("id"));

        assertThat(json.getString("message"), equalTo("Client updated."));
        assertThat(json.get("client.name"), equalTo("Josef"));
        assertThat(json.get("client.email"), equalTo("josef@mai.com"));
        assertThat(json.get("client.description"), equalTo("Kecy na nic"));
        assertThat(json.getLong("client.id"), equalTo(client.getLong("id")));

        JsonPath updatedClientJson = clientOperations.checkClientByUserIdClientId(client.getLong("id"));
        assertThat(updatedClientJson.get("name"), equalTo("Josef"));
        assertThat(updatedClientJson.get("email"), equalTo("josef@mai.com"));
        assertThat(updatedClientJson.get("description"), equalTo("Kecy na nic"));
    }

    @Test
    void editClientOnlyName() {
        JsonPath client = clientOperations.createAndReturnRandomNameEmailDescriptionClient();
        JsonPath json = clientOperations.editNameAttributeClientById(client.getLong("id"));

        String origEmail = client.getString("email");
        String origDesc = client.getString("description");
        Long origId = client.getLong("id");
        Long origUserId = client.getLong("userId");

        assertThat(json.getString("message"), equalTo("Client updated."));
        assertThat(json.get("client.name"), equalTo("Josef"));
        assertThat(json.get("client.email"), equalTo(origEmail));
        assertThat(json.get("client.description"), equalTo(origDesc));
        assertThat(json.getLong("client.id"), equalTo(origId));

        JsonPath updatedClientJson = clientOperations.checkClientByUserIdClientId(client.getLong("id"));
        assertThat(updatedClientJson.get("name"), equalTo("Josef"));
        assertThat(updatedClientJson.get("email"), equalTo(origEmail));
        assertThat(updatedClientJson.get("description"), equalTo(origDesc));
        assertThat(updatedClientJson.getLong("id"), equalTo(origId));
        assertThat(updatedClientJson.getLong("userId"), equalTo(origUserId));
    }

}
