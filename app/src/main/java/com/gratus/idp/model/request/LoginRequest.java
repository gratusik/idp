package com.gratus.idp.model.request;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.gratus.idp.R;

import java.io.Serializable;

import javax.inject.Inject;

public class LoginRequest  extends BaseObservable implements Serializable {
    private String username;
    private String password;
    private Integer usernameError;
    private Integer passwordError;
    private TextWatcher usernameTextWatcher;
    private TextWatcher passwordTextWatcher;
    private boolean usernameChange;
    private boolean passwordChange;
    private boolean animationButton,visibilityProgressBar,visibilityButton;


    @Inject
    public LoginRequest() {
    }


    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getUsernameError() {
        return usernameError;
    }

    public void setUsernameError(Integer usernameError) {
        this.usernameError = usernameError;
    }

    public Integer getPasswordError() {
        return passwordError;
    }

    public void setPasswordError(Integer passwordError) {
        this.passwordError = passwordError;
    }

    public boolean isUsernameChange() {
        return usernameChange;
    }

    public boolean isPasswordChange() {
        return passwordChange;
    }

    public void setUsernameChange(boolean usernameChange) {
        this.usernameChange = usernameChange;
    }

    public void setPasswordChange(boolean passwordChange) {
        this.passwordChange = passwordChange;
    }

    public boolean isAnimationButton() {
        return animationButton;
    }

    public void setAnimationButton(boolean animationButton) {
        this.animationButton = animationButton;
    }

    public boolean isVisibilityProgressBar() {
        return visibilityProgressBar;
    }

    public void setVisibilityProgressBar(boolean visibilityProgressBar) {
        this.visibilityProgressBar = visibilityProgressBar;
    }

    public boolean isVisibilityButton() {
        return visibilityButton;
    }

    public void setVisibilityButton(boolean visibilityButton) {
        this.visibilityButton = visibilityButton;
    }

    public boolean isEmailValid() {
        if(getUsername()!=null) {
            if (Patterns.EMAIL_ADDRESS.matcher(getUsername()).matches()) {
                setUsernameChange(false);
                setUsernameError(R.string.email_none);
                notifyChange();
                return true;
            } else {
                setUsernameChange(true);
                setUsernameError(R.string.email_empty);
                notifyChange();
                return false;
            }
        }
        else {
                setUsernameChange(true);
                setUsernameError(R.string.email_empty);
                notifyChange();
                return false;
            }
    }
    public boolean isPasswordLengthGreaterThan7() {
        if(getPassword()!=null) {
            if (getPassword().length() > 7) {
                setPasswordChange(false);
                setPasswordError(R.string.password_none);
                notifyChange();
                return true;
            } else {
                setPasswordChange(true);
                setPasswordError(R.string.password_empty);
                notifyChange();
                return false;
            }
        }
        else {
            setPasswordChange(true);
            setPasswordError(R.string.password_empty);
            notifyChange();
            return false;
        }
    }


    @Bindable
    public TextWatcher getUsernameTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                    setUsername(s.toString());
                    isEmailValid();
            }
        };
    }

    @Bindable
    public TextWatcher getPasswordTextWatcher() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing.
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                setPassword(s.toString());
                isPasswordLengthGreaterThan7();
            }
        };
    }

    public void getOnClickButton() {
        setAnimationButton(true);
        notifyChange();
    }

    public void getProgressVisibility(boolean b) {
        setVisibilityProgressBar(b);
        notifyChange();
    }

    public void getButtonVisibility(boolean b) {
        setVisibilityButton(b);
        notifyChange();
    }
}
