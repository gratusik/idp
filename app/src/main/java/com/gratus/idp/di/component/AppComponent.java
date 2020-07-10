package com.gratus.idp.di.component;

import android.app.Application;

import com.gratus.idp.BaseApplication;
import com.gratus.idp.di.modules.ActivityBindingModule;
import com.gratus.idp.di.modules.InternetModule;
import com.gratus.idp.di.modules.PrefModule;
import com.gratus.idp.view.activity.EditProfileActivity;
import com.gratus.idp.view.activity.LoginActivity;
import com.gratus.idp.di.modules.ContextModule;
import com.gratus.idp.di.modules.NetworkModule;
import com.gratus.idp.view.activity.MainActivity;
import com.gratus.idp.view.activity.ProfileActivity;
import com.gratus.idp.view.activity.ResetPasswordActivity;
import com.gratus.idp.view.activity.SignUpActivity;
import com.gratus.idp.view.activity.SplashActivity;
import com.gratus.idp.view.fragment.HomeFragment;
import com.gratus.idp.view.fragment.ReportListFragment;
import com.gratus.idp.view.fragment.SettingsFragment;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;
import dagger.android.support.DaggerApplication;

@Singleton
@Component(modules = {NetworkModule.class, PrefModule.class, InternetModule.class, ContextModule.class,
        AndroidSupportInjectionModule.class, ActivityBindingModule.class})
public interface AppComponent  extends AndroidInjector<DaggerApplication> {

    void inject(BaseApplication application);

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder application(Application application);
        AppComponent build();
        Builder internetModule(InternetModule internetModule);
    }
}
