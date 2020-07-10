package com.gratus.idp.util.networkManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;

public class NetworkOnlineCheck implements NetworkHelper {
    private  ConnectivityManager connectivityManager;

    @Inject
    public NetworkOnlineCheck(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }
    @Override
    public boolean isNetworkOnline() {
        return isConnected();
    }

    public boolean isConnected() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }
}
