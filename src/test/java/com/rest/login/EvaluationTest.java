package com.rest.login;

import com.rest.login.data.UserSession;
import com.rest.login.models.Board;
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
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.rest.login.UserSignupTest.USER_NAME;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static com.rest.login.operations.EvaluationOperations.DESCRIPTION;

@Slf4j
@SpringBootTest
public class EvaluationTest {

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


    Logger log = LoggerFactory.getLogger(EvaluationTest.class);

    JsonPath client = null;
    JsonPath client2 = null;

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
    void createEvaluationWithDescription() {
        JsonPath evaluation = evaluationOperations.createEvaluationWithDescription(client.getLong("id"));

        assertThat(evaluation.getString("evaluation.status"), equalTo("NEW"));
        assertThat(evaluation.getString("evaluation.description"), equalTo(DESCRIPTION));
        assertThat(evaluation.getString("evaluation.result"), equalTo(null));
    }

    @Test
    void createEvaluationWithoutDescription() {
        log.info(client.prettify());
        JsonPath evaluation = evaluationOperations.createEvaluationWithoutDescription(client.getLong("id"));
        log.info(evaluation.prettify());
        assertThat(evaluation.getString("evaluation.status"), equalTo("NEW"));
        assertThat(evaluation.getString("evaluation.description"), equalTo(null));
        assertThat(evaluation.getString("evaluation.result"), equalTo(null));
    }

    @Test
    void createEvaluationsAndListThem() {
        Long clientId = client.getLong("id");
        int COUNT = 4;

        evaluationOperations.createMoreEvaluationsAtOnce(COUNT, clientId);
        JsonPath json = evaluationOperations.getAllEvaluationsByClientId(clientId);

        assertThat(json.getList("evaluationsList").size(), equalTo(COUNT));
    }

    @Test
    void create2ClientsWithDifferentNumberOfEvaluations() {
        Long firstClientId = client.getLong("id");
        int evalCount1 = 4;
        int evalCount2 = 2;

        evaluationOperations.createMoreEvaluationsAtOnce(evalCount1, firstClientId);

        JsonPath firstClientjson = evaluationOperations.getAllEvaluationsByClientId(firstClientId);
        int count1 = firstClientjson.getList("evaluationsList").size();
        log.info("Klient 1 počet evaluations: " + count1);

        client2 = clientOperations.createAndReturnRandomNameClient();
        Long secondClientId = client2.getLong("id");

        evaluationOperations.createMoreEvaluationsAtOnce(evalCount2, secondClientId);

        JsonPath secondClientjson = evaluationOperations.getAllEvaluationsByClientId(secondClientId);
        int count2 = secondClientjson.getList("evaluationsList").size();
        assertThat(count1, equalTo(evalCount1));
        assertThat(count2, equalTo(evalCount2));

        evaluationOperations.createMoreEvaluationsAtOnce(3, secondClientId);

        secondClientjson = evaluationOperations.getAllEvaluationsByClientId(secondClientId);
        count2 = secondClientjson.getList("evaluationsList").size();
        assertThat(count2, equalTo(5));
        log.info("Klient 2 počet evaluations: " + count2);
    }

    @Test
    void addingToNonexistingClient() {
        Long NON_EXISTING_CLIENT_ID = client.getLong("id") + 1;
        JsonPath json = evaluationOperations.createEvaluationWithoutDescription(NON_EXISTING_CLIENT_ID);
        log.info(json.prettify());
        assertThat(json.getString("message"), equalTo(NON_EXISTING_CLIENT_MESSAGE));
    }

    @Test
    void create2EvaluationsAndBoards() {
        JsonPath evaluation = evaluationOperations.createEvaluationWithDescription(client.getLong("id"));
        JsonPath evaluation2 = evaluationOperations.createEvaluationWithDescription(client.getLong("id"));

        Long evaluationId = evaluation.getLong("evaluation.id");
        List<Board> list = boardRepository.findByEvaluation_id(evaluationId);
        long count = list.stream().filter(board -> Objects.equals(board.getEvaluation().getId(), evaluationId)).count();

        assertThat(list.size(), equalTo(10));
        assertThat(count, equalTo(10L));

        Long evaluation2Id = evaluation2.getLong("evaluation.id");
        List<Board> list2 = boardRepository.findByEvaluation_id(evaluation2Id);
        long count2 = list2.stream().filter(board -> Objects.equals(board.getEvaluation().getId(), evaluation2Id)).count();

        assertThat(list2.size(), equalTo(10));
        assertThat(count2, equalTo(10L));

        log.info(list.stream().map(Board::getId).collect(Collectors.toList()).toString());
        log.info(list2.stream().map(Board::getId).collect(Collectors.toList()).toString());
    }


}