package com.gratus.idp.view.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.gratus.idp.BaseApplication;
import com.gratus.idp.R;
import com.gratus.idp.databinding.ActivityForgotPasswordBinding;
import com.gratus.idp.util.pref.AppPreferencesHelper;
import com.gratus.idp.view.base.BaseActivity;
import com.gratus.idp.viewModel.activity.ResetPasswordViewModel;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import javax.inject.Inject;

import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;

public class ResetPasswordActivity extends BaseActivity {
    private ActivityForgotPasswordBinding activityForgotPasswordBinding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ResetPasswordViewModel resetPasswordViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityForgotPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password);
      //  ((BaseApplication) getApplicationContext()).getAppComponent().inject(this);
        resetPasswordViewModel = ViewModelProviders.of(this, viewModelFactory).get(ResetPasswordViewModel.class);
        activityForgotPasswordBinding.setResetPasswordViewModel(resetPasswordViewModel);
        activityForgotPasswordBinding.setLifecycleOwner(this);
        resetPasswordViewModel.getResetPasswordRequest().setUsername(getPrefs().getUsername());
        resetPasswordViewModel.getResetPasswordRequest().getProgressVisibility(false);
        resetPasswordViewModel.getResetPasswordRequest().getButtonVisibility(true);
        resetPasswordViewModel.getApiSuccessResponseMutableLiveData().observe(this, apiSuccessResponse -> {
            hideKeyboard();
            getDelay();
            if(apiSuccessResponse.getSuccess()){
                getPrefs().setUsername(resetPasswordViewModel.getResetPasswordRequest().getUsername());
                getPrefs().setPassword(resetPasswordViewModel.getResetPasswordRequest().getPassword());
                DynamicToast.makeSuccess(ResetPasswordActivity.this, apiSuccessResponse.getMessage()).show();
                intentLoginActivity();
            }
            else{
                if(apiSuccessResponse.getStatus()==NETWORK_CODE_EXP){
                    showSnack(true, activityForgotPasswordBinding.parent);
                }
                else {
                    DynamicToast.makeError(ResetPasswordActivity.this, apiSuccessResponse.getMessage()).show();
                }
            }
        });
        activityForgotPasswordBinding.backArrowImg.setOnClickListener(v -> {
            onBackPressed();
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
            resetPasswordViewModel.getResetPasswordRequest().getProgressVisibility(false);
            resetPasswordViewModel.getResetPasswordRequest().getButtonVisibility(true);
        }, 100);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected, activityForgotPasswordBinding.parent);
        setIntial(false);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
