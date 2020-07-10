package com.gratus.idp.model.response;

import com.google.gson.annotations.JsonAdapter;

import java.util.List;

public class FilterResponse {
    private int status;
    private boolean success;
    private String message;
    private List<OutputCycle> cycle;

    public FilterResponse() {
    }

    public FilterResponse(int status, boolean success, String message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }


    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<OutputCycle> getCycle() {
        return cycle;
    }

    public void setCycle(List<OutputCycle> cycle) {
        this.cycle = cycle;
    }
}
