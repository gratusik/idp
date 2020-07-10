package com.gratus.idp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.gratus.idp.BaseApplication;
import com.gratus.idp.R;
import com.gratus.idp.databinding.ActivitySignUpBinding;
import com.gratus.idp.view.base.BaseActivity;
import com.gratus.idp.viewModel.activity.SignUpViewModel;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import javax.inject.Inject;

import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;

public class SignUpActivity extends BaseActivity {
    private ActivitySignUpBinding activitySignUpBinding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private SignUpViewModel signUpViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activitySignUpBinding = DataBindingUtil.setContentView(this, R.layout.activity_sign_up);
      //  ((BaseApplication) getApplicationContext()).getAppComponent().inject(this);
        signUpViewModel = ViewModelProviders.of(this, viewModelFactory).get(SignUpViewModel.class);
        activitySignUpBinding.setSignUpViewModel(signUpViewModel);
        activitySignUpBinding.setLifecycleOwner(this);
        signUpViewModel.getRegistrationRequest().getProgressVisibility(false);
        signUpViewModel.getRegistrationRequest().getButtonVisibility(true);
        signUpViewModel.getApiSuccessResponseMutableLiveData().observe(this, apiSuccessResponse -> {
            hideKeyboard();
            getDelay();
            if(apiSuccessResponse.getSuccess()){
                DynamicToast.makeSuccess(SignUpActivity.this, apiSuccessResponse.getMessage()).show();
                intentLoginActivity();
            }
            else{
                if(apiSuccessResponse.getStatus()==NETWORK_CODE_EXP){
                    showSnack(true, activitySignUpBinding.parent);
                }
                else {
                    DynamicToast.makeError(SignUpActivity.this, apiSuccessResponse.getMessage()).show();
                }
            }
        });
        activitySignUpBinding.backArrowImg.setOnClickListener(v -> {
            intentLoginActivity();
        });
        activitySignUpBinding.loginBt.setOnClickListener(v -> {
            intentLoginActivity();
        });
    }

    private void intentLoginActivity() {
        Intent intent = new Intent(this,LoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void getDelay() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            signUpViewModel.getRegistrationRequest().getProgressVisibility(false);
            signUpViewModel.getRegistrationRequest().getButtonVisibility(true);
        }, 100);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected, activitySignUpBinding.parent);
        setIntial(false);
    }

    @Override
    public void onBackPressed() {
        intentLoginActivity();
    }
}
