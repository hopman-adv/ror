package com.rest.login.operations;

import com.rest.login.data.UserSession;
import com.rest.login.payload.request.AddClientRequest;
import com.rest.login.payload.request.LoginRequest;
import com.rest.login.payloads.TestUserPayload;
import com.sun.xml.bind.v2.TODO;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.fluttercode.datafactory.impl.DataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

import static com.rest.login.data.UserSession.TOKEN;
import static com.rest.login.data.UserSession.USER_ID;
import static com.rest.login.payload.request.AddClientRequest.*;
import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

@Component
public class ClientOperations {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserSession userSession;

    DataFactory fake = new DataFactory();

    Logger log = LoggerFactory.getLogger(ClientOperations.class);

    private JsonPath addClient(AddClientRequest addClientRequest) {
        return given().header("Authorization", "Bearer "+ TOKEN)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(addClientRequest)
                .when().
                post("https://localhost:8443/api/data/users/{id}/clients", USER_ID)
                .jsonPath();
    }

    private JsonPath addClient(AddClientRequest addClientRequest, String userId, String token) {
        return given().header("Authorization", "Bearer "+ token)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(addClientRequest)
                .when().
                post("https://localhost:8443/api/data/users/{id}/clients", userId)
                .jsonPath();
    }


    private JsonPath deleteClient(Long clientId) {
        return given().header("Authorization", "Bearer "+ TOKEN)
                .relaxedHTTPSValidation()
                .when().
                delete("https://localhost:8443/api/data/users/{userId}/clients/{clientId}", USER_ID, clientId)
                .jsonPath();
    }

    private JsonPath deleteClient(Long clientId, Long userId, String token) {

        return given().header("Authorization", "Bearer "+ token)
                .relaxedHTTPSValidation()
                .when().
                delete("https://localhost:8443/api/data/users/{userId}/clients/{clientId}", userId, clientId)
                .jsonPath();
    }


    private JsonPath editClient(Long clientId, AddClientRequest addClientRequest) {

        return given().header("Authorization", "Bearer "+ TOKEN)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(addClientRequest)
                .when().
                put("https://localhost:8443/api/data/users/{userId}/clients/{clientId}", USER_ID, clientId)
                .jsonPath();
    }

    private JsonPath editClient(Long clientId, AddClientRequest addClientRequest, Long userId, String token) {

        return given().header("Authorization", "Bearer "+ token)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(addClientRequest)
                .when().
                put("https://localhost:8443/api/data/users/{userId}/clients/{clientId}", userId, clientId)
                .jsonPath();
    }


    private JsonPath getClientByUserIdClientId(Long clientId) {
        return given().header("Authorization", "Bearer "+ TOKEN)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .when().
                get("https://localhost:8443/api/data/users/{id}/clients/{clientId}", USER_ID, clientId)
                .jsonPath();
    }

    private JsonPath getClientByUserIdClientId(Long clientId, String userId, String token) {
        return given().header("Authorization", "Bearer "+ token)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .when().
                get("https://localhost:8443/api/data/users/{id}/clients/{clientId}", userId, clientId)
                .jsonPath();
    }

    public JsonPath checkClientByUserIdClientId(Long clientId) {
        JsonPath json = getClientByUserIdClientId(clientId);
        log.info(json.prettify());
        return json;
    }

    public JsonPath checkClientByUserIdClientId(Long clientId, String userId, String token) {
        JsonPath json = getClientByUserIdClientId(clientId, userId, token);
        log.info(json.prettify());
        return json;
    }

    //TODO: Client requests...

    public JsonPath createAndReturnRandomNameClient() {
        String name = fake.getName();
        log.info("NAME: "+name);
        return addClient(createNameRequest(name));
    }

    public JsonPath createAndReturnRandomNameClient(String userId, String token) {
        String name = fake.getName();
        log.info("NAME: "+name);
        return addClient(createNameRequest(name), userId, token);
    }

    public JsonPath createAndReturnRandomNameEmailClient() {
        String mail = fake.getEmailAddress();
        log.info(mail);
        return addClient(createNameEmailRequest(fake.getName(), mail));
    }

    public JsonPath createAndReturnRandomNameDescriptionClient() {
        String name = fake.getName();
        log.info("NAME: "+name);
        return addClient(createNameDescriptionRequest(name, fake.getRandomText(100)));
    }

    public JsonPath createAndReturnRandomNameEmailDescriptionClient() {
        return addClient(createFullRequest(fake.getName(), fake.getEmailAddress(), fake.getRandomText(100)));
    }

    public JsonPath createAndReturnMissingNameClient() {
        return addClient(createMissingNameRequest(fake.getEmailAddress(), fake.getRandomText(100)));
    }

    public JsonPath createAndReturnSpecificNameClient(String name) {
        return addClient(createFullRequest(name, "testemail@mail.com", "TEST Dummy text for client."));
    }

    public JsonPath createAndReturnSpecificEmailClient(String email) {
        return addClient(createFullRequest("test-client", email, "TEST Dummy text for client."));
    }

    public JsonPath createAndReturnSpecificDescriptionClient(String description) {
        return addClient(createFullRequest("test-client", "testemail@mail.com", description));
    }

    public JsonPath deleteClientById(Long clientId) {
        JsonPath json = deleteClient(clientId);
        log.info(json.prettify());
        return json;
    }

    public JsonPath editAllAttributesClientById(Long clientId) {
        JsonPath json = editClient(clientId, createFullRequest("Josef", "josef@mai.com", "Kecy na nic"));
        log.info(json.prettify());
        return json;
    }

    public JsonPath editNameAttributeClientById(Long clientId) {
        JsonPath json = editClient(clientId, createNameRequest("Josef"));
        log.info(json.prettify());
        return json;
    }

}
