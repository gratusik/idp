package com.gratus.idp.di.modules;

import android.content.Context;
import android.content.SharedPreferences;

import com.gratus.idp.util.pref.AppPreferencesHelper;
import com.gratus.idp.util.pref.PreferencesHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

import static com.gratus.idp.util.constants.AppConstants.PREF_NAME;

@Module(includes = ViewModelModule.class)
public abstract class PrefModule {
    @Provides
    @Singleton
    static PreferencesHelper providePreferencesHelper(AppPreferencesHelper appPreferencesHelper) {
        return appPreferencesHelper;
    }
}
