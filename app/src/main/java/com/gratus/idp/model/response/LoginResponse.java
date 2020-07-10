package com.gratus.idp.model.response;

import java.io.Serializable;

public class LoginResponse implements Serializable {
    private int status;
    private boolean success;
    private String message;
    private Token token;
    private String path;
    private boolean update;

    public LoginResponse() {
    }

    public LoginResponse(int status, boolean success, String message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isUpdate() {
        return update;
    }

    public void setUpdate(boolean update) {
        this.update = update;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Token getToken() {
        return token;
    }

    public void setToken(Token token) {
        this.token = token;
    }
}
