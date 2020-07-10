package com.gratus.idp.viewModel.activity;

import android.os.Handler;

import androidx.lifecycle.MutableLiveData;

import com.gratus.idp.model.common.CyclePathNew;
import com.gratus.idp.model.request.LoginRequest;
import com.gratus.idp.model.request.PathRequest;
import com.gratus.idp.model.request.RegistrationRequest;
import com.gratus.idp.model.response.ApiSuccessResponse;
import com.gratus.idp.model.response.LoginResponse;
import com.gratus.idp.model.response.PathResponse;
import com.gratus.idp.repository.DownloadRepo;
import com.gratus.idp.repository.LoginRepo;
import com.gratus.idp.repository.PathRepo;
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


public class PathViewModel extends BaseViewModel {

    private PathRepo pathRepo;
    private final MutableLiveData<PathResponse>  pathResponseMutableLiveData= new MutableLiveData<>();
    private PathRequest pathRequest = new PathRequest();
    private CyclePathNew cyclePathNew;

    @Inject
    public PathViewModel(PathRepo pathRepo) {
        this.pathRepo = pathRepo;
    }

    public MutableLiveData<PathResponse> getPathResponseMutableLiveData() {
        return pathResponseMutableLiveData;
    }

    public PathRequest getPathRequest() {
        return pathRequest;
    }

    public CyclePathNew getCyclePathNew() {
        return cyclePathNew;
    }

    public void setCyclePathNew(CyclePathNew cyclePathNew) {
        this.cyclePathNew = cyclePathNew;
    }

    public void hitPath(){
        if(isNetworkConnected()) {
                getCompositeDisposable().add(pathRepo.getPathResponse(pathRequest)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableSingleObserver<PathResponse>() {
                            @Override
                            public void onSuccess(PathResponse pathResponse) {
                                getPathResponseMutableLiveData().setValue(pathResponse);
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (e instanceof HttpException) {
                                    HttpException exception = (HttpException) e;
                                    Response response = exception.response();
                                    try {
                                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                                        System.out.println(jObjError);
                                        getPathResponseMutableLiveData().setValue(new PathResponse(jObjError.getInt("status"),
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
}
