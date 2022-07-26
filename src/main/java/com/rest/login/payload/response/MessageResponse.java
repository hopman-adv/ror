package com.rest.login.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rest.login.dto.AnswerDto;
import com.rest.login.dto.ClientDTO;
import com.rest.login.dto.EvaluationDTO;
import com.rest.login.models.Evaluation;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {
    private String message;
    private ClientDTO response;
    private List<EvaluationDTO> evaluationsList;
    private List<AnswerDto> answerDtoList;
    private EvaluationDTO evaluation;
    private List<UserDetails> userDetailsList;
    private UserDetails userDetails;

    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(String message, ClientDTO clientDTO) {
        this.message = message;
        this.response = clientDTO;
    }

    public MessageResponse(String message, EvaluationDTO evaluation) {
        this.message = message;
        this.evaluation = evaluation;
    }

    public MessageResponse(String message, UserDetails userDetails) {
        this.message = message;
        this.userDetails = userDetails;
    }

    public MessageResponse(String message, List<UserDetails> userDetailsList) {
        this.message = message;
        this.userDetailsList = userDetailsList;
    }

    public static MessageResponse createMessageResponseWithEvaluationDTOs(String message, List<EvaluationDTO> evaluationsList) {
        MessageResponse messageResponse = new MessageResponse(message);
        messageResponse.setEvaluationsList(evaluationsList);
        return messageResponse;
    }

    public static MessageResponse createMessageResponseWithAnswerDTOs(String message, List<AnswerDto> answerDtoList) {
        MessageResponse messageResponse = new MessageResponse(message);
        messageResponse.setAnswerDtoList(answerDtoList);
        return messageResponse;
    }

    public List<AnswerDto> getAnswerDtoList() {
        return answerDtoList;
    }

    public void setAnswerDtoList(List<AnswerDto> answerDtoList) {
        this.answerDtoList = answerDtoList;
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ClientDTO getClient() {
        return response;
    }

    public void setClient(ClientDTO clientDTO) {
        this.response = clientDTO;
    }

    public List<EvaluationDTO> getEvaluationsList() {
        return evaluationsList;
    }

    public void setEvaluationsList(List<EvaluationDTO> evaluationsList) {
        this.evaluationsList = evaluationsList;
    }

    public ClientDTO getResponse() {
        return response;
    }

    public void setResponse(ClientDTO response) {
        this.response = response;
    }

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }

    public void setEvaluationDTO(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public List<UserDetails> getUserDetailsList() {
        return userDetailsList;
    }

    public void setUserDetailsList(List<UserDetails> userDetailsList) {
        this.userDetailsList = userDetailsList;
    }
}
