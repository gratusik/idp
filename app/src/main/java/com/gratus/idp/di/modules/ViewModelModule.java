package com.gratus.idp.di.modules;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.gratus.idp.di.key.ViewModelKey;
import com.gratus.idp.viewModel.activity.EditProfileViewModel;
import com.gratus.idp.viewModel.activity.LoginViewModel;
import com.gratus.idp.viewModel.activity.MainViewModel;
import com.gratus.idp.viewModel.activity.PathViewModel;
import com.gratus.idp.viewModel.activity.ProfileViewModel;
import com.gratus.idp.viewModel.activity.ResetPasswordViewModel;
import com.gratus.idp.viewModel.activity.SignUpViewModel;
import com.gratus.idp.viewModel.activity.SplashViewModel;
import com.gratus.idp.di.factory.ViewModelFactory;
import com.gratus.idp.viewModel.fragment.HomeViewModel;
import com.gratus.idp.viewModel.fragment.ReportListViewModel;
import com.gratus.idp.viewModel.fragment.SettingsViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel.class)
    abstract ViewModel bindLoginViewModel(LoginViewModel loginViewModel);
    @Binds
    @IntoMap
    @ViewModelKey(SplashViewModel.class)
    abstract ViewModel bindSplashViewModel(SplashViewModel splashViewModel);
    @Binds
    @IntoMap
    @ViewModelKey(ResetPasswordViewModel.class)
    abstract ViewModel bindResetPasswordViewModel(ResetPasswordViewModel resetPasswordViewModel);
    @Binds
    @IntoMap
    @ViewModelKey(SignUpViewModel.class)
    abstract ViewModel bindSignUpViewModel(SignUpViewModel signUpViewModel);
    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel.class)
    abstract ViewModel bindProfileViewModel(ProfileViewModel profileViewModel);
    @Binds
    @IntoMap
    @ViewModelKey(EditProfileViewModel.class)
    abstract ViewModel bindEditProfileViewModel(EditProfileViewModel editProfileViewModel);
    @Binds
    @IntoMap
    @ViewModelKey(MainViewModel.class)
    abstract ViewModel bindMainViewModel(MainViewModel mainViewModel);
    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    abstract ViewModel bindHomeViewModel(HomeViewModel homeViewModel);
    @Binds
    @IntoMap
    @ViewModelKey(ReportListViewModel.class)
    abstract ViewModel bindReportViewModel(ReportListViewModel reportListViewModel);
    @Binds
    @IntoMap
    @ViewModelKey(SettingsViewModel.class)
    abstract ViewModel bindSettingsViewModel(SettingsViewModel settingsViewModel);
    @Binds
    @IntoMap
    @ViewModelKey(PathViewModel.class)
    abstract ViewModel bindPathViewModel(PathViewModel pathViewModel);
    @Binds
    abstract ViewModelProvider.Factory bindFactory(ViewModelFactory factory);

}