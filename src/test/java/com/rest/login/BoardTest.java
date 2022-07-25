package com.rest.login;

import com.rest.login.data.UserSession;
import com.rest.login.enums.EResponses;
import com.rest.login.models.Board;
import com.rest.login.operations.BoardAndAnswerOperations;
import com.rest.login.operations.ClientOperations;
import com.rest.login.operations.EvaluationOperations;
import com.rest.login.operations.UserOperations;
import com.rest.login.repository.BoardRepository;
import groovy.util.logging.Slf4j;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.rest.login.UserSignupTest.USER_NAME;
import static com.rest.login.operations.EvaluationOperations.DESCRIPTION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@SpringBootTest
public class BoardTest {

    static String AUTH_URL = "https://localhost:8443/api/auth/";
    static String BASE_URL = "https://localhost:8443/api/data/";
    static String NON_EXISTING_CLIENT_MESSAGE = "Client was not found.";

    @Autowired
    private UserSession userSession;

    @Autowired
    private UserOperations userOperations;

    @Autowired
    private ClientOperations clientOperations;

    @Autowired
    private EvaluationOperations evaluationOperations;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
            private BoardAndAnswerOperations boardAndAnswerOperations;


    Logger log = LoggerFactory.getLogger(BoardTest.class);

    JsonPath client = null;
    JsonPath client2 = null;

    private final Long NON_EXISTING_ID = -1L;

    @BeforeEach
    void createUser() {
        userOperations.createAndLoginTesterUser(AUTH_URL);
        client = clientOperations.createAndReturnRandomNameClient();
    }

    @AfterEach
    void cleanupUsers() {
        if (client2 == null) {
            userOperations.deleteUserByUsernameAndClientId(USER_NAME, client.getLong("id"));
        } else {
            Long id1 = client.getLong("id");
            Long id2 = client2.getLong("id");
            userOperations.deleteAllBoards();
            userOperations.deleteEvaluationsFromClient(id1);
            userOperations.deleteEvaluationsFromClient(id2);

            userOperations.deleteClientById(id1);
            userOperations.deleteClientById(id2);
            userOperations.deleteUserByUsername(USER_NAME);
        }
    }

    @Test
    void getAnswersFromNonExistingBoard() {
        JsonPath evaluation = evaluationOperations.createEvaluationWithDescription(client.getLong("id"));

        //TODO: až bude controller pro získání all boards -> boardAndAnswerOperations.getAnswersFromBoardById()
        JsonPath json = boardAndAnswerOperations.getAnswersFromBoardById(NON_EXISTING_ID);
        assertThat(json.getString("message"), equalTo(EResponses.BOARD_NOT_FOUND.getMessage()));
        log.info(json.prettify());
    }

}