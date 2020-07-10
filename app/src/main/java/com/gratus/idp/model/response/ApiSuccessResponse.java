package com.gratus.idp.model.response;

import java.io.Serializable;

public class ApiSuccessResponse implements Serializable {
    private int status;
    private boolean success;
    private String message;

    public ApiSuccessResponse() {
    }

    public ApiSuccessResponse(int status, boolean success, String message) {
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

    public boolean getSuccess() {
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
}
