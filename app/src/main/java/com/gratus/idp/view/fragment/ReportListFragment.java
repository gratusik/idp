package com.gratus.idp.view.fragment;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.gratus.idp.R;
import com.gratus.idp.databinding.FragmentReportListingBinding;
import com.gratus.idp.model.common.CyclePathNew;
import com.gratus.idp.model.response.OutputCycle;
import com.gratus.idp.view.activity.PathActivity;
import com.gratus.idp.view.adapter.ReportListAdapter;
import com.gratus.idp.view.interfaces.adapter.ReportListListener;
import com.gratus.idp.view.base.BaseFragement;
import com.gratus.idp.viewModel.fragment.ReportListViewModel;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;

public class ReportListFragment extends BaseFragement implements ReportListListener {

    private FragmentReportListingBinding fragmentReportListingBinding;
    private View mRootView;
    public static final int VIEW_TYPE_NORMAL = 1;
    public static final int VIEW_TYPE_EMPTY = 0;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ReportListViewModel reportListViewModel;
    @Inject
    ReportListAdapter reportListAdapter;
    @Inject
    LinearLayoutManager mLayoutManager;

    public ReportListFragment() {
    }
    public static ReportListFragment newInstance(String param1, String param2) {
        ReportListFragment fragment = new ReportListFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reportListViewModel = ViewModelProviders.of(this, viewModelFactory).get(ReportListViewModel.class);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentReportListingBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_report_listing, container, false);
        mRootView = fragmentReportListingBinding.getRoot();
        fragmentReportListingBinding.setReportListViewModel(reportListViewModel);
        fragmentReportListingBinding.setLifecycleOwner(this);
        fragmentReportListingBinding.secondary.startdateTv.setOnClickListener(view -> setDate(fragmentReportListingBinding.secondary.startdateTv));
        fragmentReportListingBinding.secondary.enddateTv.setOnClickListener(view -> setDate(fragmentReportListingBinding.secondary.enddateTv));
        fragmentReportListingBinding.secondary.startTimeTv.setOnClickListener(view -> setTime(fragmentReportListingBinding.secondary.startTimeTv));
        fragmentReportListingBinding.secondary.endTimeTv.setOnClickListener(view -> setTime(fragmentReportListingBinding.secondary.endTimeTv));
        reportListAdapter.setmListener(this);
        fragmentReportListingBinding.simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                reportListAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                reportListAdapter.getFilter().filter(query);
                return false;
            }
        });
        String  jsonString1 =loadJSONFromAsset();
        setPojo(jsonString1);
        setReportUp();
        reportListViewModel.hitFilter();
        reportListViewModel.getFilterResponseMutableLiveData().observe(getActivity(), filterResponse -> {
            reportListViewModel.setReportcyclepPathNews(new ArrayList<>());
            if(filterResponse.isSuccess()){
                checkingexist(reportListViewModel.getCyclepPathNews(),filterResponse.getCycle());
                if(reportListViewModel.getReportcyclepPathNews().size()>0){
                    Collections.sort(reportListViewModel.getReportcyclepPathNews(), Collections.reverseOrder());
                    updateReport();
                }
                DynamicToast.makeSuccess(getActivity(), filterResponse.getMessage()).show();
            }
            else{
                if(reportListViewModel.getCyclepPathNews().size()>0){
                    for(CyclePathNew cyclePathNew : reportListViewModel.getCyclepPathNews()) {
                        cyclePathNew.setCount(0);
                        cyclePathNew.setColor(getActivity().getResources().getColor(R.color.green));
                        cyclePathNew.setDrawable(getActivity().getResources().getDrawable(R.drawable.count_round_green));
                        cyclePathNew.setLayoutId(VIEW_TYPE_EMPTY);
                        if (reportListViewModel.category.equalsIgnoreCase("ALL")) {
                            reportListViewModel.getReportcyclepPathNews().add(cyclePathNew);
                        } else if (reportListViewModel.category.equalsIgnoreCase("EXIST")) {
                            if (cyclePathNew.isExisting()) {
                                reportListViewModel.getReportcyclepPathNews().add(cyclePathNew);
                            }
                        } else {
                            if (!cyclePathNew.isExisting()) {
                                reportListViewModel.getReportcyclepPathNews().add(cyclePathNew);
                            }
                        }
                    }
                    updateReport();
                }
                if(filterResponse.getStatus()==NETWORK_CODE_EXP){
                    showSnack(true, fragmentReportListingBinding.parent);
                }
                else {
                    DynamicToast.makeError(getActivity(), filterResponse.getMessage()).show();
                }
            }
        });
        return mRootView;
    }

    private void setPojo(String jsonString) {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader(jsonString));
        reader.setLenient(true);
        reportListViewModel.setCyclepPathNews(new ArrayList<>());
        reportListViewModel.setReportcyclepPathNews(new ArrayList<>());
        reportListViewModel.setCyclepPathNews(Arrays.asList(gson.fromJson(reader, CyclePathNew[].class)));
        reportListViewModel.getReportcyclepPathNews().addAll(reportListViewModel.getCyclepPathNews());
    }
    private void setReportUp() {
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        fragmentReportListingBinding.recView.setLayoutManager(mLayoutManager);
        fragmentReportListingBinding.recView.setItemAnimator(new DefaultItemAnimator());
        fragmentReportListingBinding.recView.setAdapter(reportListAdapter);
    }

    private void checkingexist(List<CyclePathNew> cyclepPathNews, List<OutputCycle> cycle) {
        for(CyclePathNew cyclePathNew : cyclepPathNews) {
            boolean exist = false;
            CyclePathNew cyclePathNewCheck = new CyclePathNew();
            for (OutputCycle outputCycle : cycle) {
                if (cyclePathNew.getProperties().getID().toString().equalsIgnoreCase(outputCycle.getCyclepath_id())) {
                    exist = true;
                    cyclePathNewCheck = cyclePathNew;
                    cyclePathNewCheck.setCount(outputCycle.getCount());
                }
                if (!exist) {
                    cyclePathNewCheck = cyclePathNew;
                    cyclePathNewCheck.setCount(0);
                }
            }
            if (reportListViewModel.category.equalsIgnoreCase("ALL")) {
                addingtolist(exist, cyclePathNewCheck);
            } else if (reportListViewModel.category.equalsIgnoreCase("EXIST")) {
                if (cyclePathNewCheck.isExisting()) {
                    addingtolist(exist, cyclePathNewCheck);
                }
            } else {
                if (!cyclePathNewCheck.isExisting()) {
                    addingtolist(exist, cyclePathNewCheck);
                }
            }
        }
    }

    private void addingtolist(boolean exist, CyclePathNew cyclePathNewCheck) {
        if (exist) {
            if (cyclePathNewCheck.getCount() >= 10) {
                cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.red));
                cyclePathNewCheck.setDrawable(getActivity().getResources().getDrawable(R.drawable.count_round_red));
                cyclePathNewCheck.setLayoutId(VIEW_TYPE_NORMAL);
            }
            if (cyclePathNewCheck.getCount() >= 8 && cyclePathNewCheck.getCount() < 10) {
                cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.orange));
                cyclePathNewCheck.setDrawable(getActivity().getResources().getDrawable(R.drawable.count_round_orange));
                cyclePathNewCheck.setLayoutId(VIEW_TYPE_NORMAL);
            }
            if (cyclePathNewCheck.getCount() >= 6 && cyclePathNewCheck.getCount() < 8) {
                cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.yellow));
                cyclePathNewCheck.setDrawable(getActivity().getResources().getDrawable(R.drawable.count_round_yellow));
                cyclePathNewCheck.setLayoutId(VIEW_TYPE_NORMAL);
            }
            if (cyclePathNewCheck.getCount() >= 4 && cyclePathNewCheck.getCount() < 6) {
                cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.blue));
                cyclePathNewCheck.setDrawable(getActivity().getResources().getDrawable(R.drawable.count_round_blue));
                cyclePathNewCheck.setLayoutId(VIEW_TYPE_NORMAL);
            }
            if (cyclePathNewCheck.getCount() >= 0 && cyclePathNewCheck.getCount() < 4) {
                cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.green));
                cyclePathNewCheck.setDrawable(getActivity().getResources().getDrawable(R.drawable.count_round_green));
                cyclePathNewCheck.setLayoutId(VIEW_TYPE_NORMAL);
            }
            reportListViewModel.getReportcyclepPathNews().add(cyclePathNewCheck);
        } else {
            cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.green));
            cyclePathNewCheck.setDrawable(getActivity().getResources().getDrawable(R.drawable.count_round_green));
            cyclePathNewCheck.setLayoutId(VIEW_TYPE_EMPTY);
            reportListViewModel.getReportcyclepPathNews().add(cyclePathNewCheck);
        }
    }

    private void updateReport() {
        reportListAdapter.addItems(reportListViewModel.getReportcyclepPathNews());
    }

    private void setTime(TextView time) {
        int Hour, Minu ;
        Calendar calendar ;
        calendar = Calendar.getInstance();
        Hour = calendar.get(Calendar.HOUR_OF_DAY);
        Minu = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(),
                (TimePickerDialog.OnTimeSetListener) (view, hourOfDay, minute) -> {
                    String hours = hourOfDay + "";
                    String minutes = (minute + 1) + "";
                    if (hourOfDay < 10) {
                        hours = "0" + hourOfDay;
                    }
                    if (minute < 10) {
                        minutes = "0" + minute;
                    }
                    time.setText(hours + ":" + minutes+":00");
                }, Hour, Minu,true);
        timePickerDialog.show();
    }

    private void setDate(TextView date) {
        int Year, Month, Day ;
        Calendar calendar ;
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR) ;
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog;
         datePickerDialog = new DatePickerDialog(getActivity(),
                (DatePicker view, int year, int monthOfYear, int dayOfMonth) -> {
                    String day = dayOfMonth+"";
                    String month = (monthOfYear+1)+"";
                    if(dayOfMonth<10){
                        day = "0"+dayOfMonth;
                    }
                    if(monthOfYear+1<10){
                        month = "0"+(monthOfYear+1);
                    }
                    date.setText(day + "/" + month + "/" + year);
                }, Year, Month, Day);
        datePickerDialog.show();
    }

    @Override
    public void onItemClick(CyclePathNew cyclePathNew) {
        if(!cyclePathNew.getProperties().getStreetName().equalsIgnoreCase("No data found")) {
            Intent intent = new Intent(getActivity(), PathActivity.class);
            intent.putExtra("properties", (Serializable) cyclePathNew.getProperties());
            intent.putExtra("geometry", (Serializable) cyclePathNew.getGeometry());
            intent.putExtra("count", cyclePathNew.getCount());
            intent.putExtra("existing", cyclePathNew.isExisting());
            intent.putExtra("paths", cyclePathNew.getPaths());
            intent.putExtra("id", (Serializable) cyclePathNew.get_id());
            intent.putExtra("colour", cyclePathNew.getColor());
            startActivity(intent);
        }
    }

    @Override
    public void seachFilter() {

    }
}
