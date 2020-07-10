package com.gratus.idp.view.base;

import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.gratus.idp.BaseApplication;
import com.gratus.idp.R;
import com.gratus.idp.util.networkManager.NetworkOnlineReceiver;
import com.gratus.idp.util.networkManager.NetworkOnlineCheck;
import com.gratus.idp.util.pref.AppPreferencesHelper;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import dagger.android.support.DaggerAppCompatActivity;
import okhttp3.ResponseBody;

import static com.gratus.idp.util.constants.AppConstants.NETWORK_LOST_EXP;
import static com.gratus.idp.util.constants.AppConstants.NETWORK_ONLINE_EXP;

public abstract class BaseActivity extends DaggerAppCompatActivity implements NetworkOnlineReceiver.ConnectivityReceiverListener{
    @Inject
    AppPreferencesHelper prefs;
    @Inject
    NetworkOnlineCheck networkOnlineCheck;
    private Boolean intial = true;
    private Snackbar snackbar;

    public boolean isNetworkConnected() {
        return networkOnlineCheck.isNetworkOnline();
    }

    public void setIntial(Boolean intial) {
        this.intial = intial;
    }

    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) { InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkOnlineReceiver myReceiver =  new NetworkOnlineReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(myReceiver, filter);
        BaseApplication.getInstance().setConnectivityListener(this);
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

    public AppPreferencesHelper getPrefs() {
        return prefs;
    }



    public boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            // File futureStudioIconFile = new File(getAc(null) + File.separator + "Future Studio Icon.png");

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream=openFileOutput("cycle_path_data.json",MODE_PRIVATE);
                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    Log.d("File Download: " , fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }
    public String loadJSONFromAsset() {
        String json = null;
        InputStream is = null;
        try {
            is = openFileInput("cycle_path_data.json");
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
