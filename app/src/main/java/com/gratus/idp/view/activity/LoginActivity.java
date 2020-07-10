package com.gratus.idp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.gratus.idp.BaseApplication;
import com.gratus.idp.R;
import com.gratus.idp.databinding.ActivityLoginBinding;
import com.gratus.idp.model.response.LoginResponse;
import com.gratus.idp.util.pref.AppPreferencesHelper;
import com.gratus.idp.view.base.BaseActivity;
import com.gratus.idp.viewModel.activity.LoginViewModel;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import okhttp3.ResponseBody;

import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;


public class LoginActivity extends BaseActivity  {

    private ActivityLoginBinding activityLoginBinding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private LoginViewModel loginViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);
      //  ((BaseApplication) getApplicationContext()).getAppComponent().inject(this);
        loginViewModel = ViewModelProviders.of(this, viewModelFactory).get(LoginViewModel.class);
        activityLoginBinding.setLoginviewmodel(loginViewModel);
        activityLoginBinding.setLifecycleOwner(this);
        loginViewModel.getLoginRequest().getProgressVisibility(false);
        loginViewModel.getLoginRequest().getButtonVisibility(true);
        loginViewModel.getLoginResponseMutableLiveData().observe(this, (LoginResponse loginResponse) -> {
            hideKeyboard();
            getDelay();
            if(loginResponse.isSuccess()){
                getPrefs().setAccessToken(loginResponse.getToken().getAccessToken());
                getPrefs().setUsername(loginViewModel.getLoginRequest().getUsername());
                getPrefs().setPassword(loginViewModel.getLoginRequest().getPassword());
                DynamicToast.makeSuccess(LoginActivity.this, loginResponse.getMessage()).show();
                if(loginResponse.isUpdate()){
                    loginViewModel.hiDownload();
                }
                else{
                    String json = loadJSONFromAsset();
                    if(json!=null) {
                        intentMainActivity();
                    }
                    else{
                        loginViewModel.hiDownload();
                    }
                }
            }
            else{
                if(loginResponse.getStatus()==NETWORK_CODE_EXP){
                    showSnack(true, activityLoginBinding.parent);
                }
                else {
                    DynamicToast.makeError(LoginActivity.this, loginResponse.getMessage()).show();
                }
            }
        });
        loginViewModel.getResponseBodyMutableLiveData().observe(this, responseBody -> {
            if(writeResponseBodyToDisk(responseBody)){
               loginViewModel.hitEdit();
            }
            else{
                loginViewModel.hiDownload();
            }
        });
        loginViewModel.getApiSuccessResponseMutableLiveData().observe(this, apiSuccessResponse -> {
            hideKeyboard();
            getDelay();
            if(apiSuccessResponse.getSuccess()){
                intentMainActivity();
            }
            else{
                if(apiSuccessResponse.getStatus()==NETWORK_CODE_EXP){
                    showSnack(true, activityLoginBinding.parent);
                }
                else {
                    DynamicToast.makeError(LoginActivity.this, apiSuccessResponse.getMessage()).show();
                }
            }
        });
        activityLoginBinding.fpasswordBt.setOnClickListener(v -> {
            Intent intent = new Intent(this,ResetPasswordActivity.class);
            startActivity(intent);
        });
        activityLoginBinding.signUpBt.setOnClickListener(v -> {
            Intent intent = new Intent(this,SignUpActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private void intentMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }
    private void getDelay() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            loginViewModel.getLoginRequest().getProgressVisibility(false);
            loginViewModel.getLoginRequest().getButtonVisibility(true);
        }, 100);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected, activityLoginBinding.parent);
        setIntial(false);
    }
}
