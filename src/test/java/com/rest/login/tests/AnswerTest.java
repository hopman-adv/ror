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

import java.util.List;
import java.util.Map;

import static com.rest.login.TestUtils.getClientId;
import static com.rest.login.enums.EResponses.*;
import static com.rest.login.operations.BoardAndAnswerOperations.ANSWER_TEXT_1;
import static com.rest.login.operations.BoardAndAnswerOperations.ANSWER_TEXT_2;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

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
    void createNewAnswer() {
        //Preparation
        Long clientId = getClientId(client);
        JsonPath evaluation = evaluationOperations.createEvaluationWithDescription(clientId);
        long evalId = evaluation.getLong("evaluation.id");
        long boardId = boardAndAnswerOperations.getAllBoards(clientId, evalId).getLong("boards[0].id");
        log.info("Board ID: " + Long.toString(boardId));
        //Testing
        JsonPath answer = boardAndAnswerOperations.createAnswer(clientId, evalId, boardId);
        JsonPath answers = boardAndAnswerOperations.getAllAnswers(clientId, evalId, boardId);
        long answerId = boardId + 11; //every board has anwser (10x) + 1 new answer = 12

        assertThat(answer.getString("message"), equalTo(ANSWER_CREATED.getMessage()));
        assertThat(answer.getLong("answer.id"), greaterThan(boardId));

        assertThat(answers.getList("answers").size(), equalTo(2));
        log.info(answers.prettify());
    }

    @Test
    void editAnswer() {
        //Preparation
        Long clientId = getClientId(client);
        JsonPath evaluation = evaluationOperations.createEvaluationWithDescription(clientId);
        long evalId = evaluation.getLong("evaluation.id");
        long boardId = boardAndAnswerOperations.getAllBoards(clientId, evalId).getLong("boards[0].id");
        log.info("Board ID: " + Long.toString(boardId));

        JsonPath answer = boardAndAnswerOperations.createAnswer(clientId, evalId, boardId);
        log.info(answer.prettify());
        long answerId = answer.getLong("answer.id");
        //Testing
        //Edit 1.time
        JsonPath editAnswer = boardAndAnswerOperations.editAnswerWithPredefinedText1(clientId, evalId, boardId, answerId);
        log.info(editAnswer.prettify());
        assertThat(editAnswer.getString("message"), equalTo(ANSWER_UPDATED.getMessage()));
        assertThat(editAnswer.getString("answer.answer_text"), equalTo(ANSWER_TEXT_1));

        //Edit 2.time
        editAnswer = boardAndAnswerOperations.editAnswerWithPredefinedText2(clientId, evalId, boardId, answerId);
        log.info(editAnswer.prettify());
        assertThat(editAnswer.getString("message"), equalTo(ANSWER_UPDATED.getMessage()));
        assertThat(editAnswer.getString("answer.answer_text"), equalTo(ANSWER_TEXT_2));
    }

    @Test
    void getAllAnswersFromBoard() {
        Long clientId = getClientId(client);
        JsonPath evaluation = evaluationOperations.createEvaluationWithDescription(clientId);
        Long evalId = evaluation.getLong("evaluation.id");
        Long boardId = boardAndAnswerOperations.getAllBoards(clientId, evalId).getLong("boards[0].id");
        log.info("Board ID: " + boardId.toString());

        JsonPath answers = boardAndAnswerOperations.getAllAnswers(clientId, evalId, boardId);
        log.info(answers.prettify());

        assertThat(answers.getString("message"), equalTo(LISTING_ANSWERS_FROM_BOARD.getMessage()));
        assertThat(answers.getList("answers").size(), equalTo(1));

        assertThat(answers.getLong("answers[0].evaluationId"), equalTo(evalId));
        assertThat(answers.getLong("answers[0].boardId"), equalTo(boardId));
    }

    @Test
    void deleteAnswer() {
        //Preparation
        Long clientId = getClientId(client);
        JsonPath evaluation = evaluationOperations.createEvaluationWithDescription(clientId);
        long evalId = evaluation.getLong("evaluation.id");
        long boardId = boardAndAnswerOperations.getAllBoards(clientId, evalId).getLong("boards[0].id");
        log.info("Board ID: " + Long.toString(boardId));
        JsonPath answer = boardAndAnswerOperations.createAnswer(clientId, evalId, boardId);
        long answerId = answer.getLong("answer.id");

        //Testing
        JsonPath deletedAnswer = boardAndAnswerOperations.deleteAnswer(clientId, evalId, boardId, answerId);
        JsonPath allAnswers = boardAndAnswerOperations.getAllAnswers(clientId, evalId, boardId);

        assertThat(deletedAnswer.getString("message"), equalTo(ANSWER_DELETED.getMessage()));
        List list = allAnswers.getList("answers");
        assertThat(list.toString(), not(containsString("id="+answerId)));
    }

}