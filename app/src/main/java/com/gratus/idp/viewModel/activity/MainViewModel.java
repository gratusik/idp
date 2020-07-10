package com.gratus.idp.viewModel.activity;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.gratus.idp.model.request.LoginRequest;
import com.gratus.idp.model.response.LoginResponse;
import com.gratus.idp.repository.LoginRepo;
import com.gratus.idp.view.base.BaseViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.gratus.idp.util.constants.AppConstants.NETWORK_LOST_EXP;
import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;


public class MainViewModel extends BaseViewModel {

    private LoginRepo loginRepo;
    private final MutableLiveData<LoginResponse>  loginResponseMutableLiveData= new MutableLiveData<>();
    private LoginRequest loginRequest = new LoginRequest();

    @Inject
    public MainViewModel() {

    }
}
