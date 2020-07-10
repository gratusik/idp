/*
 *  Copyright (C) 2017 MINDORKS NEXTGEN PRIVATE LIMITED
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      https://mindorks.com/license/apache-v2
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

package com.gratus.idp.view.base;

import androidx.lifecycle.ViewModel;

import com.gratus.idp.util.networkManager.NetworkOnlineCheck;
import com.gratus.idp.util.pref.AppPreferencesHelper;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by amitshekhar on 07/07/17.
 */

public abstract class BaseViewModel extends ViewModel {
    @Inject
    AppPreferencesHelper prefs;
    @Inject
    NetworkOnlineCheck networkOnlineCheck;
    private final CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCleared() {
        if(mCompositeDisposable!=null) {
            mCompositeDisposable.dispose();
        }
        super.onCleared();
    }

    public CompositeDisposable getCompositeDisposable() {
        return mCompositeDisposable;
    }

    public boolean isNetworkConnected() {
        return networkOnlineCheck.isNetworkOnline();
    }

    public AppPreferencesHelper getPrefs() {
        return prefs;
    }
}
