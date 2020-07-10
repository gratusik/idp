package com.gratus.idp.model.request;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;

import androidx.databinding.BaseObservable;
import androidx.databinding.Bindable;

import com.gratus.idp.R;
import com.gratus.idp.util.DateTimeUtil;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationRequest  extends BaseObservable implements Serializable {
    private String username;
    private String password;
    private String confirmPassword;
    private String email;
    private String phone;
    private String empID;
    private String dob;
    private String gender;

    private Integer usernameError;
    private Integer newPasswordError;
    private Integer confirmPasswordError;
    private Integer emailError;
    private Integer phoneError;
    private Integer dobError;

    private TextWatcher usernameTextWatcher;
    private TextWatcher newPasswordTextWatcher;
    private TextWatcher confirmPasswordTextWatcher;
    private TextWatcher emailTextWatcher;
    private TextWatcher phoneTextWatcher;

    private boolean usernameChange;
    private boolean newPasswordChange;
    private boolean confirmPasswordChange;
    private boolean confirmPasswordEnable;
    private boolean emailChange;
    private boolean phoneChange;
    private boolean dobChange;

    private boolean visibilityProgressBar,visibilityButton;


    public RegistrationRequest() {
    }

    public RegistrationRequest(String username, String email, String phone, String gender, String dob) {
        this.username = username;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.dob = dob;
    }

    public RegistrationRequest(String email) {
        this.email = email;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmpID() {
        return empID;
    }

    public void setEmpID(String empID) {
        this.empID = empID;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
        notifyChange();
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

    public Integer getEmailError() {
        return emailError;
    }

    public void setEmailError(Integer emailError) {
        this.emailError = emailError;
    }

    public Integer getPhoneError() {
        return phoneError;
    }

    public void setPhoneError(Integer phoneError) {
        this.phoneError = phoneError;
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

    public boolean isEmailChange() {
        return emailChange;
    }

    public void setEmailChange(boolean emailChange) {
        this.emailChange = emailChange;
    }

    public boolean isPhoneChange() {
        return phoneChange;
    }

    public void setPhoneChange(boolean phoneChange) {
        this.phoneChange = phoneChange;
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

    public Integer getDobError() {
        return dobError;
    }

    public void setDobError(Integer dobError) {
        this.dobError = dobError;
    }

    public boolean isDobChange() {
        return dobChange;
    }

    public void setDobChange(boolean dobChange) {
        this.dobChange = dobChange;
    }

    public boolean isEmailValid() {
        if(getEmail()!=null) {
            if (Patterns.EMAIL_ADDRESS.matcher(getEmail()).matches()) {
                setEmailChange(false);
                setEmailError(R.string.email_none);
                notifyChange();
                return true;
            } else {
                setEmailChange(true);
                setEmailError(R.string.email_empty);
                notifyChange();
                return false;
            }
        }
        else {
            setEmailChange(true);
            setEmailError(R.string.email_empty);
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

    public boolean isUsernameValid() {
        if(getUsername()!=null) {
            if (getUsername().length() > 3) {
                setUsernameChange(false);
                setUsernameError(R.string.username_none);
                notifyChange();
                return true;
            } else {
                setUsernameChange(true);
                setUsernameError(R.string.username_empty);
                notifyChange();
                return false;
            }
        }
        else {
            setUsernameChange(true);
            setUsernameError(R.string.username_empty);
            notifyChange();
            return false;
        }
    }

    public boolean isPhoneValid() {
        if(getPhone()!=null) {
            if (getPhone().length() == 10) {
                setPhoneChange(false);
                setPhoneError(R.string.phone_none);
                notifyChange();
                return true;
            } else {
                setPhoneChange(true);
                setPhoneError(R.string.phone_empty);
                notifyChange();
                return false;
            }
        }
        else {
            setPhoneChange(true);
            setPhoneError(R.string.phone_empty);
            notifyChange();
            return false;
        }
    }

    public boolean isDobValid() {
        if(getDob()!=null) {
            if (DateTimeUtil.validate(getDob()))  {
                setDobChange(false);
                setDobError(R.string.dob_none);
                notifyChange();
                return true;
            } else {
                setDobChange(true);
                setDobError(R.string.dob_empty);
                notifyChange();
                return false;
            }
        }
        else {
            setDobChange(false);
            setDobError(R.string.dob_none);
            notifyChange();
            return true;
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
                isUsernameValid();
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


    @Bindable
    public TextWatcher getEmailTextWatcher() {
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
                setEmail(s.toString());
                isEmailValid();
            }
        };
    }


    @Bindable
    public TextWatcher getPhoneTextWatcher() {
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
                setPhone(s.toString());
                isPhoneValid();
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
