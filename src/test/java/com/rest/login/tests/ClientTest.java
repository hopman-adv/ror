package com.rest.login.tests;

import com.rest.login.data.UserSession;
import com.rest.login.dto.ClientDTO;
import com.rest.login.operations.ClientOperations;
import com.rest.login.operations.UserOperations;

import static com.rest.login.TestUtils.*;
import static com.rest.login.tests.DataAccessTest.CLIENT_PREFIX;
import static com.rest.login.enums.EResponses.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.rest.login.security.services.ClientService;
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

import static com.rest.login.tests.UserSignupTest.USER_NAME;
import static com.rest.login.data.UserSession.USER_ID;
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

    @Autowired
    private ClientService clientService;

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
    void getAllClientsFromUser() {
        clientOperations.createAndReturnRandomNameClient();
        clientOperations.createAndReturnRandomNameClient();
        clientOperations.createAndReturnRandomNameClient();
        clientOperations.createAndReturnRandomNameClient();

        JsonPath json = clientOperations.getAllUsersClientsFirstUser();
        assertThat(json.getList("clients").size(), equalTo(4));
    }

    @Test
    void createOnlyNameClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameClient();
        log.info(client.prettify());

        assertThat(getClientParameter(client, "name"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "id"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "email"), emptyOrNullString());
        assertThat(getClientParameter(client, "description"), emptyOrNullString());

        JsonPath json = clientOperations.checkClientByUserIdClientId(getClientId(client));
        log.info(json.prettify());
    }

    @Test
    void createNameEmailClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameEmailClient();
        log.info(client.prettify());

        assertThat(getClientParameter(client, "name"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "id"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "email"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "description"), emptyOrNullString());

        JsonPath json = clientOperations.checkClientByUserIdClientId(getClientId(client));
        log.info(json.prettify());
    }

    @Test
    void createFullClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameEmailDescriptionClient();
        log.info(client.prettify());

        assertThat(getClientParameter(client, "name"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "id"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "email"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "description"), not(emptyOrNullString()));

        JsonPath json = clientOperations.checkClientByUserIdClientId(getClientId(client));
        log.info(json.prettify());
    }

    @Test
    void createNameDescriptionClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameDescriptionClient();
        log.info(client.prettify());

        assertThat(getClientParameter(client, "name"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "id"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "email"), emptyOrNullString());
        assertThat(getClientParameter(client, "description"), not(emptyOrNullString()));

        JsonPath json = clientOperations.checkClientByUserIdClientId(getClientId(client));
        log.info(json.prettify());
    }

    @Test
    void createMissingNameClient() {
        JsonPath client = clientOperations.createAndReturnMissingNameClient();
        log.info(client.prettify());
        assertThat(client.getString("details.type"), equalTo(VALIDATION_FAILED.getMessage()));
        assertThat(client.getString("details.fieldName"), equalTo("name"));
        assertThat(client.getString("details.cause"), equalTo("Name is missing."));
    }

    @Test
    void createNameWithDiacriticsClient() {
        JsonPath client = clientOperations.createAndReturnSpecificNameClient("češký muž");
        log.info(client.prettify());

        assertThat(getClientParameter(client, "name"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "id"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "email"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "description"), not(emptyOrNullString()));

        JsonPath json = clientOperations.checkClientByUserIdClientId(getClientId(client));
        log.info(json.prettify());
    }

    @Test
    void createWrongNameClient() {
        JsonPath client = clientOperations.createAndReturnSpecificNameClient("/Test/");
        log.info(client.prettify());
        assertThat(client.getString("details.type"), equalTo(VALIDATION_FAILED.getMessage()));


        client = clientOperations.createAndReturnSpecificNameClient("#pepa");
        log.info(client.prettify());
        assertThat(client.getString("details.type"), equalTo(VALIDATION_FAILED.getMessage()));

        client = clientOperations.createAndReturnSpecificNameClient("123456789012345678901");
        log.info(client.prettify());
        assertThat(client.getString("details.type"), equalTo(VALIDATION_FAILED.getMessage()));
    }

    @Test
    void createSameNameClient() {
        JsonPath client = clientOperations.createAndReturnSpecificNameClient("test-client");
        log.info(client.prettify());

        assertThat(getClientParameter(client, "name"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "id"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "email"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "description"), not(emptyOrNullString()));
        Long firstId = getClientId(client);

        client = clientOperations.createAndReturnSpecificNameClient("test-client");

        assertThat(getClientId(client) - 1, equalTo(firstId));
        assertThat(getClientParameter(client, "name"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "id"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "email"), not(emptyOrNullString()));
        assertThat(getClientParameter(client, "description"), not(emptyOrNullString()));

        JsonPath json = clientOperations.checkClientByUserIdClientId(getClientId(client));
        log.info(json.prettify());
    }

    @Test
    void deleteClient() {
        JsonPath client = clientOperations.createAndReturnRandomNameEmailDescriptionClient();
        log.info(client.prettify());

        JsonPath response = clientOperations.deleteClientById(getClientId(client));
        log.info(response.prettify());

        assertThat(response.getString("message"), equalTo("Client deleted!"));

        JsonPath json = clientOperations.checkClientByUserIdClientId(getClientId(client));
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

        JsonPath json = clientOperations.editAllAttributesClientById(getClientId(client));

        assertThat(json.getString("message"), equalTo("Client updated."));
        assertThat(json.get(CLIENT_PREFIX + "name"), equalTo("Josef"));
        assertThat(json.get(CLIENT_PREFIX + "email"), equalTo("josef@mai.com"));
        assertThat(json.get(CLIENT_PREFIX + "description"), equalTo("Kecy na nic"));
        assertThat(json.getLong(CLIENT_PREFIX + "id"), equalTo(getClientId(client)));

        JsonPath updatedClientJson = clientOperations.checkClientByUserIdClientId(getClientId(client));
        assertThat(updatedClientJson.get(CLIENT_PREFIX + "name"), equalTo("Josef"));
        assertThat(updatedClientJson.get(CLIENT_PREFIX + "email"), equalTo("josef@mai.com"));
        assertThat(updatedClientJson.get(CLIENT_PREFIX + "description"), equalTo("Kecy na nic"));
    }

    @Test
    void editClientOnlyName() {
        JsonPath client = clientOperations.createAndReturnRandomNameEmailDescriptionClient();
        JsonPath json = clientOperations.editNameAttributeClientById(getClientId(client));

        String origEmail = getClientParameter(client, "email");
        String origDesc = getClientParameter(client, "description");
        Long origId = getClientId(client);
        Long origUserId = getClientLongParameter(client, "userId");

        assertThat(json.getString("message"), equalTo("Client updated."));
        assertThat(json.get(CLIENT_PREFIX + "name"), equalTo("Josef"));
        assertThat(json.get(CLIENT_PREFIX + "email"), equalTo(origEmail));
        assertThat(json.get(CLIENT_PREFIX + "description"), equalTo(origDesc));
        assertThat(json.getLong(CLIENT_PREFIX + "id"), equalTo(origId));

        JsonPath updatedClientJson = clientOperations.checkClientByUserIdClientId(getClientId(client));
        assertThat(updatedClientJson.get(CLIENT_PREFIX + "name"), equalTo("Josef"));
        assertThat(updatedClientJson.get(CLIENT_PREFIX + "email"), equalTo(origEmail));
        assertThat(updatedClientJson.get(CLIENT_PREFIX + "description"), equalTo(origDesc));
        assertThat(updatedClientJson.getLong(CLIENT_PREFIX + "id"), equalTo(origId));
        assertThat(updatedClientJson.getLong(CLIENT_PREFIX + "userId"), equalTo(origUserId));
    }

    @Test
    public void noClientsFoundForUser() {
        List<ClientDTO> list = clientService.getAllClientsDTO();
        assertThat(list.size(), equalTo(0));
    }

    @Test
    public void specificClientNotFoundForUser() {
        Exception exception = assertThrows(
                NoSuchElementException.class,
                () -> clientService.getClientDTOByUserIdAndClientId(Long.parseLong(USER_ID), -1L));
        log.info(exception.getMessage());
        assertEquals(exception.getMessage(), CLIENT_NOT_FOUND.getMessage());
    }

    @Test
    public void specificClientNotFoundForUserViaEndpoint() {
        JsonPath json = clientOperations.checkClientByUserIdClientId(-1L);
        log.info(json.prettify());
        assertEquals(json.getString("message"), CLIENT_NOT_FOUND.getMessage());
    }


}
