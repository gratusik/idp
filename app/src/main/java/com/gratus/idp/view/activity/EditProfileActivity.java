package com.gratus.idp.view.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.gratus.idp.BaseApplication;
import com.gratus.idp.R;
import com.gratus.idp.databinding.ActivityEditProfileBinding;
import com.gratus.idp.model.request.RegistrationRequest;
import com.gratus.idp.model.response.ProfileResponse;
import com.gratus.idp.view.base.BaseActivity;
import com.gratus.idp.viewModel.activity.EditProfileViewModel;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.Calendar;

import javax.inject.Inject;

import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;

public class EditProfileActivity extends BaseActivity{
    private ActivityEditProfileBinding activityEditProfileBinding;

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private EditProfileViewModel editProfileViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityEditProfileBinding = DataBindingUtil.setContentView(this, R.layout.activity_edit_profile);
      //  ((BaseApplication) getApplicationContext()).getAppComponent().inject(this);
        editProfileViewModel = ViewModelProviders.of(this, viewModelFactory).get(EditProfileViewModel.class);
        activityEditProfileBinding.setEditProfileViewModel(editProfileViewModel);
        activityEditProfileBinding.setLifecycleOwner(this);
        ProfileResponse profileResponse = (ProfileResponse) getIntent().getSerializableExtra("profile");
        editProfileViewModel.setRegistrationRequest(new RegistrationRequest(profileResponse.getUsername(),
                profileResponse.getEmail(),profileResponse.getPhone(),profileResponse.getGender(),profileResponse.getDob()));
        editProfileViewModel.getRegistrationRequest().getProgressVisibility(false);
        editProfileViewModel.getRegistrationRequest().getButtonVisibility(true);
        editProfileViewModel.getApiSuccessResponseMutableLiveData().observe(this, apiSuccessResponse -> {
            hideKeyboard();
            getDelay();
            if(apiSuccessResponse.getSuccess()){
                DynamicToast.makeSuccess(EditProfileActivity.this, apiSuccessResponse.getMessage()).show();
                intentProfileActivity();
            }
            else{
                if(apiSuccessResponse.getStatus()==NETWORK_CODE_EXP){
                    showSnack(true, activityEditProfileBinding.parent);
                }
                else {
                    DynamicToast.makeError(EditProfileActivity.this, apiSuccessResponse.getMessage()).show();
                }
            }
        });
        activityEditProfileBinding.backArrowImg.setOnClickListener(v -> {
            intentProfileActivity();
        });
        activityEditProfileBinding.rlBt.setOnClickListener(v -> {
            intentProfileActivity();
        });
        activityEditProfileBinding.dob.setOnClickListener(view -> setDate());
    }
    private void setDate() {
        int Year, Month, Day ;
        Calendar calendar ;
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year, monthOfYear, dayOfMonth) -> {
                    String day = dayOfMonth+"";
                    String month = (monthOfYear+1)+"";
                    if(dayOfMonth<10){
                        day = "0"+dayOfMonth;
                    }
                    if(monthOfYear+1<10){
                        month = "0"+(monthOfYear+1);
                    }
                    activityEditProfileBinding.dob.setText(day + "/" + month + "/" + year);
                }, Year, Month, Day);
        datePickerDialog.show();
    }

    private void intentProfileActivity() {
        Intent intent = new Intent(this,ProfileActivity.class);
        startActivity(intent);
        finish();
    }

    private void getDelay() {
        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            editProfileViewModel.getRegistrationRequest().getProgressVisibility(false);
            editProfileViewModel.getRegistrationRequest().getButtonVisibility(true);
        }, 100);
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected, activityEditProfileBinding.parent);
        setIntial(false);
    }

    @Override
    public void onBackPressed() {
        intentProfileActivity();
    }
}
