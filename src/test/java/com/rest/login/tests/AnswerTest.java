package com.rest.login.tests;

import com.rest.login.data.UserSession;
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

import java.util.Map;

import static com.rest.login.TestUtils.getClientId;
import static com.rest.login.enums.EResponses.LISTING_ALL_BOARDS;
import static com.rest.login.enums.EResponses.LISTING_ANSWERS_FROM_BOARD;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Slf4j
@SpringBootTest
public class AnswerTest {

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


    Logger log = LoggerFactory.getLogger(AnswerTest.class);

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
    void getAllAnswersFromBoard() {
        Long clientId = getClientId(client);
        JsonPath evaluation = evaluationOperations.createEvaluationWithDescription(clientId);
        Long evalId = evaluation.getLong("evaluation.id");
        Long boardId = boardAndAnswerOperations.getAllBoards(clientId, evalId).getLong("boards[0].id");
        log.info("Board ID: "+boardId.toString());

        JsonPath boards = boardAndAnswerOperations.getAllAnswers(clientId, evalId, boardId);
        log.info(boards.prettify());

        assertThat(boards.getString("message"), equalTo(LISTING_ANSWERS_FROM_BOARD.getMessage()));
        assertThat(boards.getList("answers").size(), equalTo(1));

        assertThat(boards.getLong("answers[0].evaluationId"), equalTo(evalId));
        assertThat(boards.getLong("answers[0].boardId"), equalTo(boardId));
        assertThat(boards.getLong("answers[0].id"), equalTo(boardId));
    }


}