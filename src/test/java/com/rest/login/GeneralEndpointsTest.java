package com.rest.login;

import io.restassured.http.ContentType;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.Matchers.*;
import static io.restassured.RestAssured.*;


@SpringBootTest
public class GeneralEndpointsTest {

    static String BASE_URL = "https://localhost:8443/api/test/";

    @Test
    void publiEndpointReturnsContent() {
        given()
                .relaxedHTTPSValidation().contentType(ContentType.TEXT).
        when()
                    .get(BASE_URL+"all").
                then().
                    statusCode(200).
                    body(equalTo("Public Content."));
    }

    @Test
    void moderatorsEndpointReturnsFailForUnlogged() {
        given()
                .relaxedHTTPSValidation().contentType(ContentType.TEXT).
        when().get(BASE_URL+"mod").
        then().
            statusCode(401);
    }

    @Test
    void usersEndpointReturnsFailForUnlogged() {
        given()
                .relaxedHTTPSValidation().contentType(ContentType.TEXT).
                when().get(BASE_URL+"user").
                then().
                statusCode(401);
    }

    @Test
    void adminEndpointReturnsFailForUnlogged() {
        given()
                .relaxedHTTPSValidation().contentType(ContentType.TEXT).
                when().get(BASE_URL+"admin").
                then().
                statusCode(401);
    }

}
