package com.gratus.idp;

import android.app.Application;
import android.content.Context;
import android.net.ConnectivityManager;

import com.gratus.idp.di.component.AppComponent;
import com.gratus.idp.di.component.DaggerAppComponent;
import com.gratus.idp.di.modules.InternetModule;
import com.gratus.idp.di.modules.PrefModule;
import com.gratus.idp.util.networkManager.NetworkOnlineReceiver;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;

public class BaseApplication extends DaggerApplication {

    private AppComponent appComponent;
    private ConnectivityManager connectivityManager;
    private static BaseApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        appComponent = DaggerAppComponent.builder().application(this)
                .internetModule(new InternetModule(connectivityManager))
                .build();
        appComponent.inject(this);
        mInstance = this;
        return appComponent;
    }
    public AppComponent getAppComponent() {
        return appComponent;
    }

    public static synchronized BaseApplication getInstance() {
        return mInstance;
    }



    public void setConnectivityListener(NetworkOnlineReceiver.ConnectivityReceiverListener listener) {
        NetworkOnlineReceiver.connectivityReceiverListener = listener;
    }
}