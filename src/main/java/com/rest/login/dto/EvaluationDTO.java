package com.rest.login.dto;

import com.rest.login.models.EStatus;
import com.rest.login.models.Evaluation;

public class EvaluationDTO {
    private Long id;
    private EStatus status;
    private String description;
    private String result;
    private Long clientId;

    public EvaluationDTO(Evaluation evaluation) {
        this.id = evaluation.getId();
        this.status = evaluation.getEvaluationStatus();
        this.description = evaluation.getDescription_info();
        this.result = evaluation.getResult();
        this.clientId = evaluation.getClient().getId();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EStatus getStatus() {
        return status;
    }

    public void setStatus(EStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }
}
