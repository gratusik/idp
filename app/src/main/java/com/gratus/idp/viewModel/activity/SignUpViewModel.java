package com.gratus.idp.viewModel.activity;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.gratus.idp.model.request.RegistrationRequest;
import com.gratus.idp.model.request.ResetPasswordRequest;
import com.gratus.idp.model.response.ApiSuccessResponse;
import com.gratus.idp.repository.ResetPasswordRepo;
import com.gratus.idp.repository.SignUpRepo;
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


public class SignUpViewModel extends BaseViewModel {

    private SignUpRepo signUpRepo;
    private final MutableLiveData<ApiSuccessResponse>  apiSuccessResponseMutableLiveData= new MutableLiveData<>();
    private RegistrationRequest registrationRequest = new RegistrationRequest();

    @Inject
    public SignUpViewModel(SignUpRepo signUpRepo) {
        this.signUpRepo = signUpRepo;
    }


    public MutableLiveData<ApiSuccessResponse> getApiSuccessResponseMutableLiveData() {
        return apiSuccessResponseMutableLiveData;
    }

    public RegistrationRequest getRegistrationRequest() {
        return registrationRequest;
    }

    public void hitSignUp(){
        if(isNetworkConnected()) {
            if (registrationRequest.isUsernameValid() && registrationRequest.isPasswordLengthGreaterThan7()
                    && registrationRequest.isConfirmPasswordLengthGreaterThan7() && registrationRequest.isEmailValid()
                    && registrationRequest.isPhoneValid()) {
                registrationRequest.getButtonVisibility(false);
                getDelay(true);
                getCompositeDisposable().add(signUpRepo.getSignUpResponse(registrationRequest)
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
        }
       else{
            getApiSuccessResponseMutableLiveData().setValue(new ApiSuccessResponse(NETWORK_CODE_EXP,
                    false, NETWORK_LOST_EXP));
        }
    }

    public void getDelay(boolean b) {
        final Handler handler = new Handler();
        handler.postDelayed(() -> registrationRequest.getProgressVisibility(b), 100);
    }
}
