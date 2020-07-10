package com.gratus.idp.view.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.gratus.idp.BaseApplication;
import com.gratus.idp.R;
import com.gratus.idp.databinding.ActivityProfileBinding;
import com.gratus.idp.view.base.BaseActivity;
import com.gratus.idp.viewModel.activity.ProfileViewModel;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import javax.inject.Inject;

import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;

public class ProfileActivity extends BaseActivity {
    private ActivityProfileBinding activityProfileBinding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ProfileViewModel profileViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_profile);
       // ((BaseApplication) getApplicationContext()).getAppComponent().inject(this);
        profileViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProfileViewModel.class);
        activityProfileBinding.setProfileViewModel(profileViewModel);
        activityProfileBinding.setLifecycleOwner(this);
        profileViewModel.hitProfile();
        profileViewModel.getProfileResponseMutableLiveData().observe(this, profileResponse -> {
            hideKeyboard();
            if(profileResponse.isSuccess()){
                DynamicToast.makeSuccess(ProfileActivity.this, profileResponse.getMessage()).show();
            }
            else{
                if(profileResponse.getStatus()==NETWORK_CODE_EXP){
                    showSnack(true, activityProfileBinding.parent);
                }
                else {
                    DynamicToast.makeError(ProfileActivity.this, profileResponse.getMessage()).show();
                }
            }
        });
        activityProfileBinding.backArrowImg.setOnClickListener(v -> {
           onBackPressed();
        });
        activityProfileBinding.rlBt.setOnClickListener(v -> {
            intentEditActivity();
        });
    }

    private void intentHomeActivity() {
//        Intent intent = new Intent(this,HomeActivity.class);
//        startActivity(intent);
//        finish();
    }

    private void intentEditActivity() {
        Intent intent = new Intent(this,EditProfileActivity.class);
        intent.putExtra("profile",profileViewModel.getProfileResponseMutableLiveData().getValue());
        startActivity(intent);
        finish();
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected, activityProfileBinding.parent);
        setIntial(false);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }
}
