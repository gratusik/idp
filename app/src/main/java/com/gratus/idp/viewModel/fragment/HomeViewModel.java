package com.gratus.idp.viewModel.fragment;

import android.widget.RadioGroup;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.gratus.idp.R;
import com.gratus.idp.model.common.CyclePathNew;
import com.gratus.idp.model.request.FilterRequest;
import com.gratus.idp.model.response.FilterResponse;
import com.gratus.idp.repository.FilterRepo;
import com.gratus.idp.util.DateTimeUtil;
import com.gratus.idp.view.base.BaseViewModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.HttpException;
import retrofit2.Response;

import static com.gratus.idp.util.constants.AppConstants.NETWORK_LOST_EXP;
import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;

public class HomeViewModel extends BaseViewModel {
    private FilterRepo filterRepo;
    public final ObservableInt bottomSheetBehaviorState = new ObservableInt(BottomSheetBehavior.STATE_HIDDEN);
    public String category = "ALL";
    private FilterRequest filterRequest = new FilterRequest();
    private final MutableLiveData<FilterResponse> filterResponseMutableLiveData= new MutableLiveData<>();
    private List<CyclePathNew> cyclepPathNews = new ArrayList<>();
    private List<CyclePathNew> reportcyclepPathNews = new ArrayList<>();
    @Inject
    public HomeViewModel(FilterRepo filterRepo) {
        this.filterRepo = filterRepo;
    }

    public ObservableInt getBottomSheetBehaviorState() {
        return bottomSheetBehaviorState;
    }

    public FilterRequest getFilterRequest() {
        return filterRequest;
    }

    public MutableLiveData<FilterResponse> getFilterResponseMutableLiveData() {
        return filterResponseMutableLiveData;
    }

    public void visiblityHour(){
        filterRequest.setVisibilityHour(true);
        filterRequest.setVisibilityDate(false);
        filterRequest.setHourwise(true);
        filterRequest.setDatewise(false);
    }

    public void visiblityDate(){
        filterRequest.setVisibilityHour(false);
        filterRequest.setVisibilityDate(true);
        filterRequest.setHourwise(false);
        filterRequest.setDatewise(true);
    }
    public List<CyclePathNew> getCyclepPathNews() {
        return cyclepPathNews;
    }

    public void setCyclepPathNews(List<CyclePathNew> cyclepPathNews) {
        this.cyclepPathNews = cyclepPathNews;
    }

    public List<CyclePathNew> getReportcyclepPathNews() {
        return reportcyclepPathNews;
    }

    public void setReportcyclepPathNews(List<CyclePathNew> reportcyclepPathNews) {
        this.reportcyclepPathNews = reportcyclepPathNews;
    }

    public void hitFilter(){
        if(isNetworkConnected()) {
            filterRequest.setVisibilityProgressBar(true);
            if(filterRequest.isHourwise()){
                if(filterRequest.getStartdate()==null){
                    filterRequest.setStartdate(DateTimeUtil.currentdate());
                    filterRequest.setEnddate(filterRequest.getStartdate());
                }
            }
            else {
                if (!filterRequest.isHourwise()) {
                    if (filterRequest.getStartdate() == null && filterRequest.getEnddate() == null) {
                        filterRequest.setStartdate(DateTimeUtil.currentdate());
                        filterRequest.setEnddate(filterRequest.getStartdate());
                        filterRequest.setStarthour("00:00:00");
                        filterRequest.setEndhour("23:59:59");
                    } else {
                        if (filterRequest.getStartdate() != null && filterRequest.getEnddate() != null) {

                        } else {
                            filterRequest.setEnddate(filterRequest.getStartdate());
                            filterRequest.setStarthour("00:00:00");
                            filterRequest.setEndhour("23:59:59");
                        }
                    }
                }
            }
            getCompositeDisposable().add(filterRepo.getLoginResponse(filterRequest)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribeWith(new DisposableSingleObserver<FilterResponse>() {
                        @Override
                        public void onSuccess(FilterResponse filterResponse) {
                            filterRequest.setVisibilityProgressBar(false);
                            onAction(false);
                            getFilterResponseMutableLiveData().setValue(filterResponse);
                        }
                        @Override
                        public void onError(Throwable e) {
                            filterRequest.setVisibilityProgressBar(false);
                            if (e instanceof HttpException) {
                                HttpException exception = (HttpException) e;
                                Response response = exception.response();
                                try {
                                    JSONObject jObjError = new JSONObject(response.errorBody().string());
                                    System.out.println(jObjError);
                                    getFilterResponseMutableLiveData().setValue(new FilterResponse(jObjError.getInt("status"),
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
            getFilterResponseMutableLiveData().setValue(new FilterResponse(NETWORK_CODE_EXP,
                    false, NETWORK_LOST_EXP));
        }
    }

    public void onAction(boolean show){
        filterRequest.setVisibilityHour(false);
        filterRequest.setVisibilityDate(true);
        filterRequest.setVisibilityProgressBar(false);
        filterRequest.setHourwise(false);
        filterRequest.setDatewise(true);
        if(show){
            bottomSheetBehaviorState.set(BottomSheetBehavior.STATE_EXPANDED);
        }
        else{
            bottomSheetBehaviorState.set(BottomSheetBehavior.STATE_HIDDEN);
        }

    }
    public void onClear(boolean show){
        filterRequest.setVisibilityHour(false);
        filterRequest.setVisibilityDate(true);
        filterRequest.setVisibilityProgressBar(false);
        filterRequest.setHourwise(false);
        filterRequest.setDatewise(true);
        filterRequest.setStartdate(null);
        hitFilter();
        if(show){
            bottomSheetBehaviorState.set(BottomSheetBehavior.STATE_EXPANDED);
        }
        else{
            bottomSheetBehaviorState.set(BottomSheetBehavior.STATE_HIDDEN);
        }
    }

    public void onSplitTypeChanged(RadioGroup radioGroup, int id) {
        if(id == R.id.all) {
            category = "ALL";
        } else if(id == R.id.exist) {
            category = "EXIST";
        }
        else if(id == R.id.unexist) {
            category = "UNEXIST";
        }
        else {
            category = "ALL";
        }
    }
}
