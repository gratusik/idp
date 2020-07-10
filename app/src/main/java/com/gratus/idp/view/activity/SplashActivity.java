package com.gratus.idp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.gratus.idp.BaseApplication;
import com.gratus.idp.R;
import com.gratus.idp.databinding.ActivitySplashBinding;
import com.gratus.idp.util.pref.AppPreferencesHelper;
import com.gratus.idp.view.base.BaseActivity;
import com.gratus.idp.viewModel.activity.SplashViewModel;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import javax.inject.Inject;

import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;

public class SplashActivity extends BaseActivity {
    private ActivitySplashBinding activitySplashBinding;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private SplashViewModel splashViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        //((BaseApplication) getApplicationContext()).getAppComponent().inject(this);
        splashViewModel = ViewModelProviders.of(this, viewModelFactory).get(SplashViewModel.class);
        activitySplashBinding.setSplashviewmodel(splashViewModel);
        activitySplashBinding.setLifecycleOwner(this);
        splashViewModel.hitLogin();
        splashViewModel.getLoginResponseMutableLiveData().observe(this, loginResponse -> {
            if(loginResponse.isSuccess()){
                getPrefs().setAccessToken(loginResponse.getToken().getAccessToken());
                DynamicToast.makeSuccess(SplashActivity.this, loginResponse.getMessage()).show();
                if(loginResponse.isUpdate()){
                    splashViewModel.hiDownload();
                }
                else{
                    String json = loadJSONFromAsset();
                    if(json!=null) {
                        intentMainActivity();
                    }
                    else{
                        splashViewModel.hiDownload();
                    }
                }
            }
            else{
                if(loginResponse.getStatus()==NETWORK_CODE_EXP){
                    showSnack(false, activitySplashBinding.parent);
                    getDelay();
                }
                else {
                    getDelay();
                }
            }
        });
        splashViewModel.getResponseBodyMutableLiveData().observe(this, responseBody -> {
            if(writeResponseBodyToDisk(responseBody)){
                splashViewModel.hitEdit();
            }
            else{
                splashViewModel.hiDownload();
            }
        });
        splashViewModel.getApiSuccessResponseMutableLiveData().observe(this, apiSuccessResponse -> {
            if(apiSuccessResponse.getSuccess()){
                intentMainActivity();
            }
            else{
                if(apiSuccessResponse.getStatus()==NETWORK_CODE_EXP){
                    showSnack(true, activitySplashBinding.parent);
                }
                else {
                    DynamicToast.makeError(SplashActivity.this, apiSuccessResponse.getMessage()).show();
                }
            }
        });
    }

    private void getDelay() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> intentActivity(), 100);
    }

    private void intentActivity() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void intentMainActivity() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected, activitySplashBinding.parent);
        setIntial(false);
    }
}
