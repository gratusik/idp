package com.gratus.idp.util.pref;

import android.content.Context;
import android.content.SharedPreferences;

import javax.inject.Inject;

import static com.gratus.idp.util.constants.AppConstants.PREF_NAME;

public class AppPreferencesHelper implements PreferencesHelper {
    private static final String Username = "usernameKey";
    private static final String Password = "passwordKey";
    private static final String Accesstoken = "accessKey";

    private final SharedPreferences mPrefs;

    @Inject
    public AppPreferencesHelper(Context context) {
        this.mPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    @Override
    public String getAccessToken() {
        return mPrefs.getString(Accesstoken, null);
    }

    @Override
    public void setAccessToken(String accessToken) {
        mPrefs.edit().putString(Accesstoken, accessToken).apply();
    }

    @Override
    public String getUsername() {
        return mPrefs.getString(Username, null);
    }

    @Override
    public void setUsername(String username) {
        mPrefs.edit().putString(Username, username).apply();
    }

    @Override
    public String getPassword() {
        return mPrefs.getString(Password, null);
    }

    @Override
    public void setPassword(String password) {
        mPrefs.edit().putString(Password, password).apply();
    }

    @Override
    public boolean isClear() {
        return false;
    }

    @Override
    public void setClear(boolean clear) {
        SharedPreferences.Editor editor = mPrefs.edit();
        editor.clear();
        editor.commit();
    }
}
