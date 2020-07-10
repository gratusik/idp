package com.gratus.idp.viewModel.activity;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.gratus.idp.model.request.LoginRequest;
import com.gratus.idp.model.request.RegistrationRequest;
import com.gratus.idp.model.response.ApiSuccessResponse;
import com.gratus.idp.model.response.LoginResponse;
import com.gratus.idp.repository.DownloadRepo;
import com.gratus.idp.repository.LoginRepo;
import com.gratus.idp.repository.SignUpRepo;
import com.gratus.idp.view.base.BaseViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.gratus.idp.util.constants.AppConstants.NETWORK_LOST_EXP;
import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;


public class LoginViewModel extends BaseViewModel {

    private LoginRepo loginRepo;
    private DownloadRepo downloadRepo;
    private final MutableLiveData<LoginResponse>  loginResponseMutableLiveData= new MutableLiveData<>();
    private final MutableLiveData<ResponseBody>  responseBodyMutableLiveData= new MutableLiveData<>();
    private LoginRequest loginRequest = new LoginRequest();
    private SignUpRepo signUpRepo;
    private final MutableLiveData<ApiSuccessResponse>  apiSuccessResponseMutableLiveData= new MutableLiveData<>();
    private RegistrationRequest registrationRequest = new RegistrationRequest();

    @Inject
    public LoginViewModel(LoginRepo loginRepo, DownloadRepo downloadRepo, SignUpRepo signUpRepo) {
        this.loginRepo = loginRepo;
        this.downloadRepo = downloadRepo;
        this.signUpRepo = signUpRepo;
    }

    public MutableLiveData<LoginResponse> getLoginResponseMutableLiveData() {
        return loginResponseMutableLiveData;
    }

    public MutableLiveData<ResponseBody> getResponseBodyMutableLiveData() {
        return responseBodyMutableLiveData;
    }

    public MutableLiveData<ApiSuccessResponse> getApiSuccessResponseMutableLiveData() {
        return apiSuccessResponseMutableLiveData;
    }

    public LoginRequest getLoginRequest() {
        return loginRequest;
    }

    public void hitLogin(){
        if(isNetworkConnected()) {
            if (loginRequest.isEmailValid() && loginRequest.isPasswordLengthGreaterThan7()) {
                loginRequest.getButtonVisibility(false);
                getDelay(true);
                getCompositeDisposable().add(loginRepo.getLoginResponse(loginRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<LoginResponse>() {
                            @Override
                            public void onSuccess(LoginResponse loginResponse) {
                                getLoginResponseMutableLiveData().setValue(loginResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof HttpException) {
                                    HttpException exception = (HttpException) e;
                                    Response response = exception.response();
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        System.out.println(jObjError);
                                        getLoginResponseMutableLiveData().setValue(new LoginResponse(jObjError.getInt("status"),
                                                false, jObjError.getString("message")));
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }));
            }
        }
       else{
            getLoginResponseMutableLiveData().setValue(new LoginResponse(NETWORK_CODE_EXP,
                    false, NETWORK_LOST_EXP));
        }
    }
    public void hiDownload(){
        if(isNetworkConnected()) {
                getCompositeDisposable().add(downloadRepo.download()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ResponseBody>() {
                            @Override
                            public void onSuccess(ResponseBody responseBody) {
                                getResponseBodyMutableLiveData().setValue(responseBody);
                            }

                            @Override
                            public void onError(Throwable e) {
                                /*if (e instanceof HttpException) {
                                    HttpException exception = (HttpException) e;
                                    Response response = exception.response();
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        System.out.println(jObjError);
                                        getLoginResponseMutableLiveData().setValue(new LoginResponse(jObjError.getInt("status"),
                                                false, jObjError.getString("message")));
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }*/
                            }
                        }));
            }
    }
    public void hitEdit(){
        if(isNetworkConnected()) {
            registrationRequest = new RegistrationRequest(loginRequest.getUsername());
                getCompositeDisposable().add(signUpRepo.getEditProfileU(registrationRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ApiSuccessResponse>() {
                            @Override
                            public void onSuccess(ApiSuccessResponse apiSuccessResponse) {
                                getApiSuccessResponseMutableLiveData().setValue(apiSuccessResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof HttpException) {
                                    HttpException exception = (HttpException) e;
                                    Response response = exception.response();
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        System.out.println(jObjError);
                                        getApiSuccessResponseMutableLiveData().setValue(new ApiSuccessResponse(jObjError.getInt("status"),
                                                false, jObjError.getString("message")));
                                    } catch (JSONException ex) {
                                        ex.printStackTrace();
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }
                        }));
            }
        else{
            getApiSuccessResponseMutableLiveData().setValue(new ApiSuccessResponse(NETWORK_CODE_EXP,
                    false, NETWORK_LOST_EXP));
        }
    }
    public void getDelay(boolean b) {
        final Handler handler = new Handler();
        handler.postDelayed(() -> loginRequest.getProgressVisibility(b), 100);
    }
}
