package com.gratus.idp.viewModel.activity;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.gratus.idp.model.request.ProfileRequest;
import com.gratus.idp.model.request.RegistrationRequest;
import com.gratus.idp.model.response.ApiSuccessResponse;
import com.gratus.idp.model.response.ProfileResponse;
import com.gratus.idp.repository.ProfileRepo;
import com.gratus.idp.repository.SignUpRepo;
import com.gratus.idp.util.pref.AppPreferencesHelper;
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


public class ProfileViewModel extends BaseViewModel {

    private ProfileRepo profileRepo;
    private final MutableLiveData<ProfileResponse>  profileResponseMutableLiveData= new MutableLiveData<>();
    private ProfileRequest profileRequest = new ProfileRequest();

    @Inject
    public ProfileViewModel(ProfileRepo profileRepo) {
        this.profileRepo = profileRepo;
    }

    public MutableLiveData<ProfileResponse> getProfileResponseMutableLiveData() {
        return profileResponseMutableLiveData;
    }

    public ProfileRequest getProfileRequest() {
        return profileRequest;
    }

    public void hitProfile(){
        if(isNetworkConnected()) {
            if (getPrefs().getUsername() != null) {
                profileRequest.setUsername(getPrefs().getUsername());
                getCompositeDisposable().add(profileRepo.getProfile(profileRequest,getPrefs().getAccessToken())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<ProfileResponse>() {
                            @Override
                            public void onSuccess(ProfileResponse profileResponse) {
                                getProfileResponseMutableLiveData().setValue(profileResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof HttpException) {
                                    HttpException exception = (HttpException) e;
                                    Response response = exception.response();
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        System.out.println(jObjError);
                                        getProfileResponseMutableLiveData().setValue(new ProfileResponse(jObjError.getInt("status"),
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
            getProfileResponseMutableLiveData().setValue(new ProfileResponse(NETWORK_CODE_EXP,
                    false, NETWORK_LOST_EXP));
        }
    }
}
