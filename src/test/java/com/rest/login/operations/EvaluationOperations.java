package com.rest.login.operations;

import com.rest.login.data.UserSession;
import com.rest.login.models.Evaluation;
import com.rest.login.payload.request.AddClientRequest;
import com.rest.login.payload.request.AddEvaluationRequest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.fluttercode.datafactory.impl.DataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import static com.rest.login.data.UserSession.*;
import static com.rest.login.payload.request.AddClientRequest.*;
import static com.rest.login.payload.request.AddEvaluationRequest.createEvaluationRequest;
import static io.restassured.RestAssured.given;

@Component
public class EvaluationOperations {

    public static final String DESCRIPTION = "Description regarding this evaluation.";
    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private UserSession userSession;

    DataFactory fake = new DataFactory();

    Logger log = LoggerFactory.getLogger(EvaluationOperations.class);

    private JsonPath addEvaluation(AddEvaluationRequest addEvaluationRequest, Long clientId, String userId, String token) {

        if (addEvaluationRequest != null) {
            return given().header("Authorization", "Bearer " + token)
                    .relaxedHTTPSValidation()
                    .contentType(ContentType.JSON)
                    .body(addEvaluationRequest)
                    .when().
                    post("https://localhost:8443/api/data/users/{id}/clients/{clientId}/evaluations", userId, clientId)
                    .jsonPath();
        } else {
            return given().header("Authorization", "Bearer " + token)
                    .relaxedHTTPSValidation()
                    .when().
                    post("https://localhost:8443/api/data/users/{id}/clients/{clientId}/evaluations", userId, clientId)
                    .jsonPath();
        }
    }

    public JsonPath getAllEvaluationsByClientId(Long clientId, String id, String token) {
        String url = "https://localhost:8443/api/data/users/" + id + "/clients/" + clientId + "/evaluations";
        log.info(url);
        return given().header("Authorization", "Bearer " + token)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .when().
                get(url)
                .jsonPath();
    }

    public JsonPath getAllEvaluationsByClientId(Long clientId) {
        String url = "https://localhost:8443/api/data/users/" + USER_ID + "/clients/" + clientId + "/evaluations";
        log.info(url);
        return given().header("Authorization", "Bearer " + TOKEN)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .when().
                get(url)
                .jsonPath();
    }

    public JsonPath getEvaluationByClientIdAndEvaluationID(Long clientId, Long evalId) {
        String url = "https://localhost:8443/api/data/users/" + USER_ID + "/clients/" + clientId + "/evaluations/" + evalId;
        log.info(url);
        return given().header("Authorization", "Bearer " + TOKEN)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .when().
                get(url)
                .jsonPath();
    }

    public JsonPath editEvaluationByClientIdAndEvaluationId(Long clientId, Long evalId, AddEvaluationRequest addEvaluationRequest) {
        String url = "https://localhost:8443/api/data/users/" + USER_ID + "/clients/" + clientId + "/evaluations/" + evalId;
        log.info(url);
        return given().header("Authorization", "Bearer " + TOKEN)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(addEvaluationRequest)
                .when().
                put(url)
                .jsonPath();
    }

    public JsonPath deleteEvaluationByClientIdAndEvaluationId(Long clientId, Long evalId) {
        String url = "https://localhost:8443/api/data/users/" + USER_ID + "/clients/" + clientId + "/evaluations/" + evalId;
        log.info(url);
        return given().header("Authorization", "Bearer " + TOKEN)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .when().
                delete(url)
                .jsonPath();
    }

    public JsonPath createEvaluationWithDescription(Long clientId) {
        return addEvaluation(createEvaluationRequest(DESCRIPTION), clientId, USER_ID, TOKEN);
    }

    public JsonPath createEvaluationWithDescriptionForSecondUsersClient(Long clientId) {
        return addEvaluation(createEvaluationRequest(DESCRIPTION), clientId, USER2_ID, TOKEN2);
    }

    public JsonPath createEvaluationWithoutDescription(Long clientId) {
        return addEvaluation(null, clientId, USER_ID, TOKEN);
    }

    public void createMoreEvaluationsAtOnce(Integer count, Long clientId) {
        for (int i = 0; i < count; i++) {
            createEvaluationWithoutDescription(clientId);
        }
    }

}
