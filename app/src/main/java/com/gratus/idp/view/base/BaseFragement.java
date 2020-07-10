package com.gratus.idp.view.base;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.snackbar.Snackbar;
import com.gratus.idp.R;
import com.gratus.idp.util.networkManager.NetworkOnlineCheck;
import com.gratus.idp.util.pref.AppPreferencesHelper;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;

import dagger.android.support.DaggerFragment;

import static com.gratus.idp.util.constants.AppConstants.NETWORK_LOST_EXP;
import static com.gratus.idp.util.constants.AppConstants.NETWORK_ONLINE_EXP;

public class BaseFragement extends DaggerFragment {
    private Boolean intial = true;
    private Snackbar snackbar;
    @Inject
    AppPreferencesHelper prefs;
    @Inject
    NetworkOnlineCheck networkOnlineCheck;

    public AppPreferencesHelper getPrefs() {
        return prefs;
    }
    public void showSnack(boolean networkConnected, View parent) {

        if (networkConnected) {
            if(!intial) {
                snackbar = Snackbar.make(parent, NETWORK_ONLINE_EXP, Snackbar.LENGTH_SHORT);
                getSnackBarCustom(snackbar.getView(),true);
            }
        } else {
            snackbar = Snackbar.make(parent, NETWORK_LOST_EXP, Snackbar.LENGTH_INDEFINITE);
            getSnackBarCustom(snackbar.getView(),false);
        }
    }
    private void getSnackBarCustom(View view, boolean b) {
        View sbView = view;
        TextView textView = (TextView) sbView.findViewById(R.id.snackbar_text);
        if(b) {
            textView.setTextColor(Color.WHITE);
            textView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            snackbar.setBackgroundTint(getResources().getColor(R.color.app_color));
        }
        else{
            textView.setTextColor(Color.YELLOW);
        }
        snackbar.show();
    }
    public boolean isNetworkConnected() {
        return networkOnlineCheck.isNetworkOnline();
    }

    public void setIntial(Boolean intial) {
        this.intial = intial;
    }

    public String loadJSONFromAsset() {
        String json = null;
        InputStream is = null;
        try {
            is = getActivity().openFileInput("cycle_path_data.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }
}
