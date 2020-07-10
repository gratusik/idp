package com.gratus.idp.di.modules;

import com.gratus.idp.view.activity.EditProfileActivity;
import com.gratus.idp.view.activity.LoginActivity;
import com.gratus.idp.view.activity.MainActivity;
import com.gratus.idp.view.activity.PathActivity;
import com.gratus.idp.view.activity.ProfileActivity;
import com.gratus.idp.view.activity.ResetPasswordActivity;
import com.gratus.idp.view.activity.SignUpActivity;
import com.gratus.idp.view.activity.SplashActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBindingModule {

    @ContributesAndroidInjector(modules = {MainFragmentBindingModule.class})
    abstract MainActivity bindMainActivity();
    @ContributesAndroidInjector
    abstract LoginActivity bindloginActivity();
    @ContributesAndroidInjector
    abstract SplashActivity bindSplashActivity();
    @ContributesAndroidInjector
    abstract ResetPasswordActivity bindResetPasswordActivity();
    @ContributesAndroidInjector
    abstract SignUpActivity bindSignUpActivity();
    @ContributesAndroidInjector
    abstract ProfileActivity bindProfileActivity();
    @ContributesAndroidInjector
    abstract EditProfileActivity bindEditProfileActivity();
    @ContributesAndroidInjector
    abstract PathActivity bindPathActivity();
}
