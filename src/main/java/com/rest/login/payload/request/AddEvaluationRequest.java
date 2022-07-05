package com.rest.login.payload.request;

import com.rest.login.models.Client;
import com.rest.login.models.EStatus;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

public class AddEvaluationRequest {

    @Size(max = 1500)
    private String description;


    public static AddEvaluationRequest createEvaluationRequest(String description) {
        AddEvaluationRequest addEvaluationRequest = new AddEvaluationRequest();
        addEvaluationRequest.setDescription(description);
        return addEvaluationRequest;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
