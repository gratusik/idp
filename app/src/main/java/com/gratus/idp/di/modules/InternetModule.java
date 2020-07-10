package com.gratus.idp.di.modules;

import android.content.Context;
import android.net.ConnectivityManager;

import com.gratus.idp.util.networkManager.NetworkHelper;
import com.gratus.idp.util.networkManager.NetworkOnlineCheck;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(includes = ViewModelModule.class)
public class InternetModule {
    private  ConnectivityManager connectivityManager;

    public InternetModule(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    @Provides
    @Singleton
    ConnectivityManager provideNetworkHelper() {
        return connectivityManager;
    }
}
