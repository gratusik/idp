package com.gratus.idp.view.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.gratus.idp.R;
import com.gratus.idp.databinding.FragmentHomeBinding;
import com.gratus.idp.databinding.FragmentSettingsBinding;
import com.gratus.idp.util.pref.AppPreferencesHelper;
import com.gratus.idp.view.activity.ProfileActivity;
import com.gratus.idp.view.activity.ResetPasswordActivity;
import com.gratus.idp.view.base.BaseFragement;
import com.gratus.idp.viewModel.fragment.HomeViewModel;
import com.gratus.idp.viewModel.fragment.SettingsViewModel;

import javax.inject.Inject;

import static com.gratus.idp.util.constants.AppConstants.PREF_NAME;

public class SettingsFragment extends BaseFragement {

    private FragmentSettingsBinding fragmentSettingsBinding;
    private View mRootView;
    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private SettingsViewModel settingsViewModel;

    public SettingsFragment() {
    }
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSettingsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_settings, container, false);
        mRootView = fragmentSettingsBinding.getRoot();
        fragmentSettingsBinding.setSettingsViewModel(settingsViewModel);
        fragmentSettingsBinding.setLifecycleOwner(this);
        fragmentSettingsBinding.name.setOnClickListener(v -> {
            goProfile();
        });
        fragmentSettingsBinding.password.setOnClickListener(v -> {
            goRestPasswrord();
        });
        fragmentSettingsBinding.logout.setOnClickListener(v -> {
            logout();
        });
        return mRootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        settingsViewModel = ViewModelProviders.of(this, viewModelFactory).get(SettingsViewModel.class);
    }

    public void goProfile(){
        Intent intent = new Intent(getActivity(), ProfileActivity.class);
        startActivity(intent);
    }

    public void goRestPasswrord(){
        Intent intent = new Intent(getActivity(), ResetPasswordActivity.class);
        startActivity(intent);
    }

    public void logout(){
        getPrefs().setClear(true);
        getActivity().finish();
        System.exit(0);
    }
}
