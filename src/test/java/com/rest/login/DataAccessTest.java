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

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.rest.login.data.UserSession.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Slf4j
@SpringBootTest
public class DataAccessTest {

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


    Logger log = LoggerFactory.getLogger(DataAccessTest.class);

    JsonPath client = null;
    JsonPath client2 = null;

    @BeforeEach
    void createUser() {
        userOperations.createAndLoginTesterUser(AUTH_URL);
        userOperations.createAndLoginSecondTesterUser(AUTH_URL);

        client = clientOperations.createAndReturnRandomNameClient();
        client2 = clientOperations.createAndReturnRandomNameClient(USER2_ID, TOKEN2);
    }

    @AfterEach
    void cleanupUsers() {
        userOperations.deleteAll();
    }

    @Test
    void userCanSeeOnlyHisClients() {
        Long id1 = client.getLong("id");
        Long id2 = client2.getLong("id");
        String name1 = client.getString("name");
        String name2 = client2.getString("name");

        JsonPath json1 = clientOperations.checkClientByUserIdClientId(id1, USER_ID, TOKEN);
        JsonPath json2 = clientOperations.checkClientByUserIdClientId(id2, USER2_ID, TOKEN2);
        JsonPath json3 = clientOperations.checkClientByUserIdClientId(id1, USER2_ID, TOKEN2);
        JsonPath json4 = clientOperations.checkClientByUserIdClientId(id2, USER_ID, TOKEN);

        assertThat(json1.getString("name"), equalTo(name1));
        assertThat(json2.getString("name"), equalTo(name2));
        assertThat(json3.getString("message"), equalTo("Error: Client not found!"));
        assertThat(json4.getString("message"), equalTo("Error: Client not found!"));
    }

    @Test
    void userCanSeeOnlyHisEvaluations() {
        Long id1 = client.getLong("id");
        Long id2 = client2.getLong("id");
        String name1 = client.getString("name");
        String name2 = client2.getString("name");

        JsonPath evaluationFirstClient1 = evaluationOperations.createEvaluationWithDescription(id1);
        JsonPath evaluationFirstClient2 = evaluationOperations.createEvaluationWithDescription(id1);
        JsonPath evaluationSecondClient1 = evaluationOperations.createEvaluationWithDescription(id2);

        long evaluationId1 = evaluationFirstClient1.getLong("evaluation.id");
        long evaluationId2 = evaluationFirstClient2.getLong("evaluation.id");
        long evaluationId3 = evaluationSecondClient1.getLong("evaluation.id");

        JsonPath firstClientEvaluationJson = evaluationOperations.getAllEvaluationsByClientId(id1);
        JsonPath secondClientEvaluationJson = evaluationOperations.getAllEvaluationsByClientId(id2);

        List<Object> list1 = firstClientEvaluationJson.getList("evaluationsList");
        List<Object> list2 = secondClientEvaluationJson.getList("evaluationsList");

        assertThat(list1.get(0).toString(), containsString("id="+evaluationId1));
        assertThat(list1.get(1).toString(), containsString("id="+evaluationId2));
        assertThat(list2.get(0).toString(), containsString("id="+evaluationId3));

        assertThat(firstClientEvaluationJson.getList("evaluationsList").size(), equalTo(2));
        assertThat(secondClientEvaluationJson.getList("evaluationsList").size(), equalTo(1));

        assertThat(list1.get(0).toString(), not(containsString("id="+evaluationId3)));
        assertThat(list1.get(1).toString(), not(containsString("id="+evaluationId3)));
        assertThat(list2.get(0).toString(), not(containsString("id="+evaluationId1)));
        assertThat(list2.get(0).toString(), not(containsString("id="+evaluationId2)));
    }

    @Test
    void userCanSeeOnlyHisBoards() {

    }

    @Test
    void userCanSeeOnlyHisAsnwers() {
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