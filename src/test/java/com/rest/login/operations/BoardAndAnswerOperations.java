package com.rest.login.operations;

import com.rest.login.data.UserSession;
import com.rest.login.payload.request.AddClientRequest;
import com.rest.login.payload.request.UpdateAnswerRequest;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import org.fluttercode.datafactory.impl.DataFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import static com.rest.login.data.UserSession.TOKEN;
import static com.rest.login.data.UserSession.USER_ID;
import static com.rest.login.payload.request.AddClientRequest.*;
import static io.restassured.RestAssured.given;

@Component
public class BoardAndAnswerOperations {

    public static final String ANSWER_TEXT_1 ="Text #1";
    public static final String ANSWER_TEXT_2 ="Text #2";

    Logger log = LoggerFactory.getLogger(BoardAndAnswerOperations.class);

    public JsonPath createAnswer(Long clientId, Long evalId, Long boardId) {
        return given().header("Authorization", "Bearer " + TOKEN)
                .relaxedHTTPSValidation()
                .when().
                post("https://localhost:8443/api/data/users/{userId}/clients/{clientId}/evaluations/{evalId}/boards/{boardId}/answers",
                        USER_ID, clientId, evalId, boardId)
                .jsonPath();
    }

    public JsonPath editAnswer(Long clientId, Long evalId, Long boardId, Long answerId, UpdateAnswerRequest updateAnswerRequest) {
        return given().header("Authorization", "Bearer " + TOKEN)
                .relaxedHTTPSValidation()
                .contentType(ContentType.JSON)
                .body(updateAnswerRequest)
                .when().
                put("https://localhost:8443/api/data/users/{userId}/clients/{clientId}/evaluations/{evalId}/boards/{boardId}/answers/{answerId}",
                        USER_ID, clientId, evalId, boardId, answerId)
                .jsonPath();
    }

    public JsonPath deleteAnswer(Long clientId, Long evalId, Long boardId, Long answerId) {
        return given().header("Authorization", "Bearer " + TOKEN)
                .relaxedHTTPSValidation()
                .when().
                delete("https://localhost:8443/api/data/users/{userId}/clients/{clientId}/evaluations/{evalId}/boards/{boardId}/answers/{answerId}",
                        USER_ID, clientId, evalId, boardId, answerId)
                .jsonPath();
    }


    public JsonPath getAllBoards(Long clientId, Long evalId) {
        return given().header("Authorization", "Bearer " + TOKEN)
                .relaxedHTTPSValidation()
                .when().
                get("https://localhost:8443/api/data/users/{userId}/clients/{clientId}/evaluations/{evalId}/boards/", USER_ID, clientId, evalId)
                .jsonPath();
    }

    public JsonPath getAllAnswers(Long clientId, Long evalId, Long boardId) {
        return given().header("Authorization", "Bearer " + TOKEN)
                .relaxedHTTPSValidation()
                .when().
                get("https://localhost:8443/api/data/users/{userId}/clients/{clientId}/evaluations/{evalId}/boards/{boardId}/answers", USER_ID, clientId, evalId, boardId)
                .jsonPath();
    }

    private JsonPath getAnswersFromBoard(Long boardId) {
        return given().header("Authorization", "Bearer " + TOKEN)
                .relaxedHTTPSValidation()
                .when().
                get("https://localhost:8443/api/data/users/{userId}/boards/{boardId}", USER_ID, boardId)
                .jsonPath();
    }

    public JsonPath editAnswerWithPredefinedText1(Long clientId, Long evalId, Long boardId, Long answerId) {
        UpdateAnswerRequest updateAnswerRequest = new UpdateAnswerRequest();
        updateAnswerRequest.setText(ANSWER_TEXT_1);
        return editAnswer(clientId, evalId, boardId, answerId, updateAnswerRequest);
    }

    public JsonPath editAnswerWithPredefinedText2(Long clientId, Long evalId, Long boardId, Long answerId) {
        UpdateAnswerRequest updateAnswerRequest = new UpdateAnswerRequest();
        updateAnswerRequest.setText(ANSWER_TEXT_2);
        return editAnswer(clientId, evalId, boardId, answerId, updateAnswerRequest);
    }


    public JsonPath getAnswersFromBoardById(Long id) {
        return getAnswersFromBoard(id);
    }

}
