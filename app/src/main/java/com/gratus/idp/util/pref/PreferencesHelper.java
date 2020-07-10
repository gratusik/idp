package com.gratus.idp.util.pref;

public interface PreferencesHelper {
    String getAccessToken();

    void setAccessToken(String accessToken);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    boolean isClear();

    void setClear(boolean clear);

}
