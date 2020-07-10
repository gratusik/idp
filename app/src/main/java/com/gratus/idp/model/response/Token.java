package com.gratus.idp.model.response;

import java.io.Serializable;

public class Token  implements Serializable {
    private String accessToken;
    private String tokenType;

    public Token() {
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
}
