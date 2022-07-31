package com.rest.login.payload.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rest.login.dto.AnswerDto;
import com.rest.login.dto.ClientDTO;
import com.rest.login.dto.EvaluationDTO;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

import static com.rest.login.enums.EResponses.*;

//TODO:1 Změnit pomocí JsonProperty jména (hlavně DTO...) + potom opravit v testech.
//TODO:2 Možná přesunout konstruktory do samotné factory.
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MessageResponse {
    private String message;
    @JsonProperty("client")
    private ClientDTO clientDTO;
    @JsonProperty("clients")
    private List<ClientDTO> clientDTOsList;
    private List<EvaluationDTO> evaluationsList;
    private List<AnswerDto> answerDtoList;
    private EvaluationDTO evaluation;
    private List<UserDetails> userDetailsList;
    private UserDetails userDetails;
    @JsonProperty("details")
    private ErrorResponse errorResponse;

    public MessageResponse(String message) {
        this.message = message;
    }

    public MessageResponse(String message, ClientDTO clientDTO) {
        this.message = message;
        this.clientDTO = clientDTO;
    }

    public MessageResponse(String message, EvaluationDTO evaluation) {
        this.message = message;
        this.evaluation = evaluation;
    }

    public MessageResponse(String message, UserDetails userDetails) {
        this.message = message;
        this.userDetails = userDetails;
    }

    public static MessageResponse createMessageResponseWithUserDetailsList(List<UserDetails> userDetailsList) {
        MessageResponse messageResponse = new MessageResponse(LISTING_ALL_USERS.getMessage());
        messageResponse.setUserDetailsList(userDetailsList);
        return messageResponse;
    }

    public static MessageResponse createMessageResponseWithClientDTOsList(List<ClientDTO> clientDTOsList) {
        MessageResponse messageResponse = new MessageResponse(LISTING_ALL_CLIENTS.getMessage());
        messageResponse.setClientDTOsList(clientDTOsList);
        return messageResponse;
    }

    public static MessageResponse createMessageResponseWithEvaluationDTOs(List<EvaluationDTO> evaluationsList) {
        MessageResponse messageResponse = new MessageResponse(LISTING_EVALUATIONS.getMessage());
        messageResponse.setEvaluationsList(evaluationsList);
        return messageResponse;
    }

    public static MessageResponse createMessageResponseWithAnswerDTOs(String message, List<AnswerDto> answerDtoList) {
        MessageResponse messageResponse = new MessageResponse(message);
        messageResponse.setAnswerDtoList(answerDtoList);
        return messageResponse;
    }

    public MessageResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ClientDTO getClientDTO() {
        return clientDTO;
    }

    public void setClientDTO(ClientDTO clientDTO) {
        this.clientDTO = clientDTO;
    }

    public List<ClientDTO> getClientDTOsList() {
        return clientDTOsList;
    }

    public void setClientDTOsList(List<ClientDTO> clientDTOsList) {
        this.clientDTOsList = clientDTOsList;
    }

    public List<EvaluationDTO> getEvaluationsList() {
        return evaluationsList;
    }

    public void setEvaluationsList(List<EvaluationDTO> evaluationsList) {
        this.evaluationsList = evaluationsList;
    }

    public List<AnswerDto> getAnswerDtoList() {
        return answerDtoList;
    }

    public void setAnswerDtoList(List<AnswerDto> answerDtoList) {
        this.answerDtoList = answerDtoList;
    }

    public EvaluationDTO getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(EvaluationDTO evaluation) {
        this.evaluation = evaluation;
    }

    public List<UserDetails> getUserDetailsList() {
        return userDetailsList;
    }

    public void setUserDetailsList(List<UserDetails> userDetailsList) {
        this.userDetailsList = userDetailsList;
    }

    public UserDetails getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetails userDetails) {
        this.userDetails = userDetails;
    }

    public ErrorResponse getErrorResponse() {
        return errorResponse;
    }

    public void setErrorResponse(ErrorResponse errorResponse) {
        this.errorResponse = errorResponse;
    }
}
