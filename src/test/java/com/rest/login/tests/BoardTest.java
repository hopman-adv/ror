package com.rest.login.tests;

import com.rest.login.data.UserSession;
import com.rest.login.enums.EResponses;
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

import static com.rest.login.TestUtils.getClientId;
import static com.rest.login.enums.EResponses.LISTING_ALL_BOARDS;
import static com.rest.login.tests.UserSignupTest.USER_NAME;
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
        userOperations.deleteAll();
    }

    @Test
    void getAllBoardsFromEvaluation() {
        Long clientId = getClientId(client);
        JsonPath evaluation = evaluationOperations.createEvaluationWithDescription(clientId);
        Long evalId = evaluation.getLong("evaluation.id");

        JsonPath json = boardAndAnswerOperations.getAllBoards(clientId, evalId);
        assertThat(json.getString("message"), equalTo(LISTING_ALL_BOARDS.getMessage()));
        assertThat(json.getList("boards").size(), equalTo(10));
    }
}