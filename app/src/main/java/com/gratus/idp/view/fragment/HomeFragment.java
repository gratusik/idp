package com.gratus.idp.view.fragment;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableField;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.gratus.idp.R;

import com.gratus.idp.databinding.FragmentHomeBinding;
import com.gratus.idp.model.common.CyclePathNew;
import com.gratus.idp.model.common.MarkerArray;
import com.gratus.idp.model.common.MarkerArrayPath;
import com.gratus.idp.model.common.PolylineModel;
import com.gratus.idp.model.response.OutputCycle;
import com.gratus.idp.view.base.BaseFragement;
import com.gratus.idp.viewModel.fragment.HomeViewModel;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import static android.os.SystemClock.sleep;
import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;

public class HomeFragment extends BaseFragement implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener {
    private FragmentHomeBinding fragmentHomeBinding;
    private View mRootView;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private GoogleMap mMap;
    final Handler handler = new Handler();
    private HomeViewModel homeViewModel;
    ArrayList<PolylineModel> polylineModels = new ArrayList<>();
    private ClusterManager<MarkerArray> mClusterManager;
    ArrayList<MarkerArrayPath> marker = new ArrayList<MarkerArrayPath>();
    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        homeViewModel = ViewModelProviders.of(this, viewModelFactory).get(HomeViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);
        mRootView = fragmentHomeBinding.getRoot();
        fragmentHomeBinding.setHomeViewModel(homeViewModel);
        fragmentHomeBinding.setLifecycleOwner(this);
        fragmentHomeBinding.secondary.startdateTv.setOnClickListener(view -> setDate(fragmentHomeBinding.secondary.startdateTv));
        fragmentHomeBinding.secondary.enddateTv.setOnClickListener(view -> setDate(fragmentHomeBinding.secondary.enddateTv));
        fragmentHomeBinding.secondary.startTimeTv.setOnClickListener(view -> setTime(fragmentHomeBinding.secondary.startTimeTv));
        fragmentHomeBinding.secondary.endTimeTv.setOnClickListener(view -> setTime(fragmentHomeBinding.secondary.endTimeTv));
        fragmentHomeBinding.map.onCreate(savedInstanceState);
        fragmentHomeBinding.map.onResume();
        fragmentHomeBinding.map.getMapAsync(this);
        String jsonString = loadJSONFromAsset();
        setPojo(jsonString);
        homeViewModel.hitFilter();
        homeViewModel.getFilterResponseMutableLiveData().observe(getActivity(), filterResponse -> {
            homeViewModel.setReportcyclepPathNews(new ArrayList<>());
            if (filterResponse.isSuccess()) {
                checkingexist(homeViewModel.getCyclepPathNews(), filterResponse.getCycle());
                if (homeViewModel.getReportcyclepPathNews().size() > 0) {
                    Collections.sort(homeViewModel.getReportcyclepPathNews(), Collections.reverseOrder());
                    getDelayRemoveandAdd();
                }
                DynamicToast.makeSuccess(getActivity(), filterResponse.getMessage()).show();
            } else {
                if (filterResponse.getStatus() == NETWORK_CODE_EXP) {
                    showSnack(true, fragmentHomeBinding.parent);
                } else {
                    DynamicToast.makeError(getActivity(), filterResponse.getMessage()).show();
                    if (homeViewModel.getCyclepPathNews().size() > 0) {
                        for (CyclePathNew cyclePathNew : homeViewModel.getCyclepPathNews()) {
                            cyclePathNew.setCount(0);
                            cyclePathNew.setDrawable(getActivity().getResources().getDrawable(R.drawable.count_round_green));
                            if (cyclePathNew.getCount() > 0) {
                                if (homeViewModel.category.equalsIgnoreCase("ALL")) {
                                    if (cyclePathNew.isExisting()) {
                                        cyclePathNew.setColor(getActivity().getResources().getColor(R.color.pink));
                                    } else {
                                        cyclePathNew.setColor(getActivity().getResources().getColor(R.color.violet));
                                    }
                                    homeViewModel.getReportcyclepPathNews().add(cyclePathNew);
                                } else if (homeViewModel.category.equalsIgnoreCase("EXIST")) {
                                    if (cyclePathNew.isExisting()) {
                                        cyclePathNew.setColor(getActivity().getResources().getColor(R.color.pink));
                                        homeViewModel.getReportcyclepPathNews().add(cyclePathNew);
                                    }
                                } else {
                                    if (!cyclePathNew.isExisting()) {
                                        cyclePathNew.setColor(getActivity().getResources().getColor(R.color.violet));
                                        homeViewModel.getReportcyclepPathNews().add(cyclePathNew);
                                    }
                                }
                            }
                        }
                        getDelayRemoveandAdd();
                    }
                }
            }
        });
        return mRootView;
    }

    private void setPojo(String jsonString) {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new StringReader(jsonString));
        reader.setLenient(true);
        homeViewModel.setCyclepPathNews(new ArrayList<>());
        homeViewModel.setReportcyclepPathNews(new ArrayList<>());
        homeViewModel.setCyclepPathNews(Arrays.asList(gson.fromJson(reader, CyclePathNew[].class)));
        homeViewModel.getReportcyclepPathNews().addAll(homeViewModel.getCyclepPathNews());
    }

    private void setTime(TextView time) {
        int Hour, Minu;
        Calendar calendar;
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
                    time.setText(hours + ":" + minutes + ":00");
                }, Hour, Minu, true);
        timePickerDialog.show();
    }

    private void checkingexist(List<CyclePathNew> cyclepPathNews, List<OutputCycle> cycle) {
        for (CyclePathNew cyclePathNew : cyclepPathNews) {
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
            if (cyclePathNew.getCount() > 0) {
                if (homeViewModel.category.equalsIgnoreCase("ALL")) {
                    addingtolist(exist, cyclePathNewCheck);
                } else if (homeViewModel.category.equalsIgnoreCase("EXIST")) {
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
    }

    private void addingtolist(boolean exist, CyclePathNew cyclePathNewCheck) {
        if (homeViewModel.category.equalsIgnoreCase("ALL")) {
            if (cyclePathNewCheck.isExisting()) {
                cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.pink));
            } else {
                cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.violet));
            }
            homeViewModel.getReportcyclepPathNews().add(cyclePathNewCheck);
        } else {
            if (exist) {
                if (cyclePathNewCheck.getCount() >= 10) {
                    cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.red));
                }
                if (cyclePathNewCheck.getCount() >= 8 && cyclePathNewCheck.getCount() < 10) {
                    cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.orange));
                }
                if (cyclePathNewCheck.getCount() >= 6 && cyclePathNewCheck.getCount() < 8) {
                    cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.yellow));
                }
                if (cyclePathNewCheck.getCount() >= 4 && cyclePathNewCheck.getCount() < 6) {
                    cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.blue));
                }
                if (cyclePathNewCheck.getCount() >= 0 && cyclePathNewCheck.getCount() < 4) {
                    cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.green));
                }
                homeViewModel.getReportcyclepPathNews().add(cyclePathNewCheck);
            } else {
                cyclePathNewCheck.setColor(getActivity().getResources().getColor(R.color.green));
                homeViewModel.getReportcyclepPathNews().add(cyclePathNewCheck);
            }
        }
    }

    private void setDate(TextView date) {
        int Year, Month, Day;
        Calendar calendar;
        calendar = Calendar.getInstance();
        Year = calendar.get(Calendar.YEAR);
        Month = calendar.get(Calendar.MONTH);
        Day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog;
        datePickerDialog = new DatePickerDialog(getActivity(),
                (DatePicker view, int year, int monthOfYear, int dayOfMonth) -> {
                    String day = dayOfMonth + "";
                    String month = (monthOfYear + 1) + "";
                    if (dayOfMonth < 10) {
                        day = "0" + dayOfMonth;
                    }
                    if (monthOfYear + 1 < 10) {
                        month = "0" + (monthOfYear + 1);
                    }
                    date.setText(day + "/" + month + "/" + year);
                }, Year, Month, Day);
        datePickerDialog.show();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng coordinate = new LatLng(45.0703, 7.6869);
        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
                coordinate, 10);

        mClusterManager = new ClusterManager<>(getActivity(), googleMap);
        mMap.animateCamera(location);
        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnPolylineClickListener(this);

    }

    private void getDelayRemoveandAdd() {
        mMap.clear();
        polylineModels = new ArrayList<>();
        mClusterManager = new ClusterManager<>(getActivity(), mMap);
        marker = new ArrayList<MarkerArrayPath>();
        Runnable runnable;
        for (CyclePathNew cyclePathNew : homeViewModel.getReportcyclepPathNews()) {
            ArrayList<LatLng> latLngs = new ArrayList<>();
            PolylineOptions polylineOptions = new PolylineOptions();
            for (int i = 0; i < cyclePathNew.getGeometry().getLatitudes().size(); i++) {
                for (int j = 0; j < cyclePathNew.getGeometry().getLatitudes().get(i).size(); j++) {
                    Double lat = cyclePathNew.getGeometry().getLatitudes().get(i).get(j);
                    Double lon = cyclePathNew.getGeometry().getLongitudes().get(i).get(j);
                    LatLng latLng = new LatLng(lat, lon);
                    latLngs.add(latLng);
                }
                LatLng first = latLngs.get(0);
                System.out.println(first.latitude + "" + first.longitude);
                LatLng last = latLngs.get(latLngs.size() - 1);
                System.out.println(last.latitude + "" + last.longitude);
                String streetname = cyclePathNew.getProperties().getStreetName()+"-"+cyclePathNew.getCount();
                if (cyclePathNew.getProperties().getStreetName() == null || cyclePathNew.getProperties().getStreetName().isEmpty()) {
                    streetname = "No Street Name"+"-"+cyclePathNew.getCount();
                }
                mClusterManager.addItem(new MarkerArray(first, streetname));
                mClusterManager.addItem(new MarkerArray(last, streetname));
                marker.add(new MarkerArrayPath(first,streetname));
                marker.add(new MarkerArrayPath(last,streetname));
                List<PatternItem> pattern = Arrays.<PatternItem>asList(new Dot(), new Gap(15f));
                polylineOptions = new PolylineOptions()
                        .addAll(latLngs)
                        .color(cyclePathNew.getColor())
                        .width(10f);
                        //.pattern(pattern);;
            }
            polylineModels.add(new PolylineModel(polylineOptions, cyclePathNew));
        }
        runnable = new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
                for (PolylineModel polylineModel : polylineModels) {
                    Polyline polyline = mMap.addPolyline(polylineModel.getPolylineOptions());
                    polyline.setClickable(true);
                }
                setupMap();
            }
        };
        handler.postDelayed(runnable, 10);
    }
    private  void setupMap() {
        /*Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
                for(MarkerArrayPath markerArray: marker) {
                    mMap.addMarker(new MarkerOptions().position(markerArray.getPosition()).
                            flat(true).
                            title(markerArray.getTitle()).
                            icon(bitmapDescriptorFromVector(getActivity(), R.drawable.cust_marker)));
                }
            }
        };
        handler.postDelayed(runnable, 10);*/

       // VenueMarkerRender renderer = new VenueMarkerRender (getActivity(),mMap,mClusterManager);
        //mClusterManager.setRenderer(renderer);
        mClusterManager.cluster();
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.setAnimation(false);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);
    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        marker.showInfoWindow();
    }

    @Override
    public void onPolylineClick(Polyline polyline) {
        polyline.setColor(R.color.app_color);
        polyline.getPoints();
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), 50, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    public class VenueMarkerRender extends DefaultClusterRenderer<MarkerArray> {

        private final Context mContext;

        public VenueMarkerRender(Context context, GoogleMap map, ClusterManager<MarkerArray> clusterManager) {
            super(context, map, clusterManager);
            mContext = context;
        }

        @Override
        protected void onBeforeClusterItemRendered(MarkerArray item, MarkerOptions markerOptions) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
            markerOptions.title(item.getTitle());
        }
        @Override
        protected void onBeforeClusterRendered(Cluster<MarkerArray> cluster, MarkerOptions markerOptions) {
            //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
           // markerOptions.title(String.valueOf(cluster.getItems().size()));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<MarkerArray> cluster){
            return cluster.getSize() > 1;
        }

    }
}
