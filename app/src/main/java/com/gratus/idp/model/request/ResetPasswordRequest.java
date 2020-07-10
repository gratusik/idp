package com.gratus.idp.model.request;


import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.gratus.idp.R;

import java.io.Serializable;

import javax.inject.Inject;

public class ResetPasswordRequest extends BaseObservable implements Serializable {
    private String username;
    private String password;
    private String confirmPassword;
    private Integer usernameError;
    private Integer newPasswordError;
    private Integer confirmPasswordError;
    private TextWatcher usernameTextWatcher;
    private TextWatcher newPasswordTextWatcher;
    private TextWatcher confirmPasswordTextWatcher;
    private boolean usernameChange;
    private boolean newPasswordChange;
    private boolean confirmPasswordChange;
    private boolean confirmPasswordEnable;
    private boolean visibilityProgressBar,visibilityButton;


    @Inject
    public ResetPasswordRequest() {
    }


    public ResetPasswordRequest(String username, String password) {
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

    public String getConfirmPassword() {
        return confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public Integer getUsernameError() {
        return usernameError;
    }

    public void setUsernameError(Integer usernameError) {
        this.usernameError = usernameError;
    }

    public Integer getNewPasswordError() {
        return newPasswordError;
    }

    public void setNewPasswordError(Integer newPasswordError) {
        this.newPasswordError = newPasswordError;
    }

    public Integer getConfirmPasswordError() {
        return confirmPasswordError;
    }

    public void setConfirmPasswordError(Integer confirmPasswordError) {
        this.confirmPasswordError = confirmPasswordError;
    }

    public boolean isUsernameChange() {
        return usernameChange;
    }

    public void setUsernameChange(boolean usernameChange) {
        this.usernameChange = usernameChange;
    }

    public boolean isNewPasswordChange() {
        return newPasswordChange;
    }

    public void setNewPasswordChange(boolean newPasswordChange) {
        this.newPasswordChange = newPasswordChange;
    }

    public boolean isConfirmPasswordChange() {
        return confirmPasswordChange;
    }

    public void setConfirmPasswordChange(boolean confirmPasswordChange) {
        this.confirmPasswordChange = confirmPasswordChange;
    }

    public boolean isConfirmPasswordEnable() {
        return confirmPasswordEnable;
    }

    public void setConfirmPasswordEnable(boolean confirmPasswordEnable) {
        this.confirmPasswordEnable = confirmPasswordEnable;
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
                setNewPasswordChange(false);
                setNewPasswordError(R.string.password_none);
                setConfirmPasswordEnable(true);
                notifyChange();
                return true;
            } else {
                setNewPasswordChange(true);
                setNewPasswordError(R.string.password_empty);
                setConfirmPasswordEnable(false);
                notifyChange();
                return false;
            }
        }
        else {
            setNewPasswordChange(true);
            setNewPasswordError(R.string.password_empty);
            setConfirmPasswordEnable(false);
            notifyChange();
            return false;
        }
    }
    public boolean isConfirmPasswordLengthGreaterThan7() {
        if(getConfirmPassword()!=null) {
            if(getConfirmPassword().equals(getPassword())) {
                if (getConfirmPassword().length() > 7) {
                    setConfirmPasswordChange(false);
                    setConfirmPasswordError(R.string.password_none);
                    notifyChange();
                    return true;
                } else {
                    setConfirmPasswordChange(true);
                    setConfirmPasswordError(R.string.password_empty);
                    notifyChange();
                    return false;
                }
            }
            else{
                setConfirmPasswordChange(true);
                setConfirmPasswordError(R.string.password_match);
                notifyChange();
                return false;
            }
        }
        else {
            setConfirmPasswordChange(true);
            setConfirmPasswordError(R.string.password_empty);
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
    public TextWatcher getNewPasswordTextWatcher() {
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

    @Bindable
    public TextWatcher getConfirmPasswordTextWatcher() {
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
                setConfirmPassword(s.toString());
                isConfirmPasswordLengthGreaterThan7();
            }
        };
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
