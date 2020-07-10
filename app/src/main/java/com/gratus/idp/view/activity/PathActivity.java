package com.gratus.idp.view.activity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import androidx.annotation.RequiresApi;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.gratus.idp.R;
import com.gratus.idp.databinding.ActivityPathBinding;
import com.gratus.idp.model.common.CyclePathNew;
import com.gratus.idp.model.common.Geometry;
import com.gratus.idp.model.common.Id;
import com.gratus.idp.model.common.MarkerArray;
import com.gratus.idp.model.common.MarkerArrayPath;
import com.gratus.idp.model.common.PolylineModel;
import com.gratus.idp.model.common.Properties;
import com.gratus.idp.model.response.GraphValue;
import com.gratus.idp.util.chart.MyValueFormatter;
import com.gratus.idp.view.base.BaseActivity;
import com.gratus.idp.viewModel.activity.PathViewModel;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import static com.gratus.idp.util.constants.AppConstantsCode.NETWORK_CODE_EXP;

public class PathActivity extends BaseActivity implements OnMapReadyCallback, OnChartValueSelectedListener {
    private ActivityPathBinding activityPathBinding;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private PathViewModel pathViewModel;
    final Handler handler = new Handler();
    private GoogleMap mMap;
    private CyclePathNew cyclePathNew;
    ArrayList<PolylineModel> polylineModels = new ArrayList<>();
    ArrayList<MarkerArrayPath> marker = new ArrayList<MarkerArrayPath>();
    private ClusterManager<MarkerArray> mClusterManager;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityPathBinding = DataBindingUtil.setContentView(this, R.layout.activity_path);
        //  ((BaseApplication) getApplicationContext()).getAppComponent().inject(this);
        pathViewModel = ViewModelProviders.of(this, viewModelFactory).get(PathViewModel.class);
        activityPathBinding.setPathViewModel(pathViewModel);
        activityPathBinding.setLifecycleOwner(this);
        Properties properties = (Properties) getIntent().getSerializableExtra("properties");
        Geometry geometry = (Geometry) getIntent().getSerializableExtra("geometry");
        int count =  getIntent().getIntExtra("count",0);
        boolean existing = getIntent().getBooleanExtra("existing",false);
        String paths = getIntent().getStringExtra("paths");
        Id id = (Id) getIntent().getSerializableExtra("id");
        int color =  getIntent().getIntExtra("colour",0);
        cyclePathNew = new CyclePathNew(id,paths,existing,properties,geometry,count,color);
        pathViewModel.setCyclePathNew(cyclePathNew);
        activityPathBinding.pathView.onCreate(savedInstanceState);
        activityPathBinding.pathView.onResume();
        activityPathBinding.pathView.getMapAsync(this);
        activityPathBinding.backArrowImg.setOnClickListener(v -> onBackPressed());
        pathViewModel.getPathRequest().setPathid(Math.toIntExact(cyclePathNew.getProperties().getID()));
        pathViewModel.hitPath();
        pathViewModel.getPathResponseMutableLiveData().observe(this, pathResponse -> {
            if(pathResponse.isSuccess()){
                for(GraphValue graphValue : pathResponse.getHour()) {
                    if(graphValue.getCount()>0) {
                        activityPathBinding.past24Hour.setVisibility(View.VISIBLE);
                        activityPathBinding.chart1.setVisibility(View.VISIBLE);
                        set24HourChart(pathResponse.getHour());
                        break;
                    }
                }
                for(GraphValue graphValue : pathResponse.getOneweek()) {
                    if(graphValue.getCount()>0) {
                        activityPathBinding.pastOneWeek.setVisibility(View.VISIBLE);
                        activityPathBinding.chart2.setVisibility(View.VISIBLE);
                        setOneWeekChart(pathResponse.getOneweek());
                        break;
                    }
                }
                for(GraphValue graphValue : pathResponse.getFourweek()) {
                    if(graphValue.getCount()>0) {
                        activityPathBinding.past4Week.setVisibility(View.VISIBLE);
                        activityPathBinding.chart3.setVisibility(View.VISIBLE);
                        setFourWeekChart(pathResponse.getFourweek());
                        break;
                    }
                }
                for(GraphValue graphValue : pathResponse.getHourinmonth()) {
                    if(graphValue.getCount()>0) {
                        activityPathBinding.partOneMonth.setVisibility(View.VISIBLE);
                        activityPathBinding.chart4.setVisibility(View.VISIBLE);
                        setHourinmonth(pathResponse.getHourinmonth());
                        break;
                    }
                }
                for(GraphValue graphValue : pathResponse.getYear()) {
                    if(graphValue.getCount()>0) {
                        activityPathBinding.pastYear.setVisibility(View.VISIBLE);
                        activityPathBinding.chart5.setVisibility(View.VISIBLE);
                        setYearChart(pathResponse.getYear());
                        break;
                    }
                }
               for(GraphValue graphValue : pathResponse.getHourinyear()) {
                    if(graphValue.getCount()>0) {
                        activityPathBinding.partYearMonth.setVisibility(View.VISIBLE);
                        activityPathBinding.chart6.setVisibility(View.VISIBLE);
                        setHourinYear(pathResponse.getHourinyear());
                        break;
                    }
                }
                for(GraphValue graphValue : pathResponse.getTenyear()) {
                    if(graphValue.getCount()>0) {
                        activityPathBinding.tenYear.setVisibility(View.VISIBLE);
                        activityPathBinding.chart7.setVisibility(View.VISIBLE);
                        setTenYearChart(pathResponse.getTenyear());
                        break;
                    }
                }
            }
            else{
                if(pathResponse.getStatus()==NETWORK_CODE_EXP){
                    showSnack(true, activityPathBinding.parent);
                }
                else {
                    DynamicToast.makeError(PathActivity.this, pathResponse.getMessage()).show();
                }
            }
        });
    }
    private void setHourinYear(List<GraphValue> hourinmonth) {
        ArrayList<BarEntry> values = new ArrayList<>();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        int i=0;
        for(GraphValue graphValue : hourinmonth){
            values.add(new BarEntry(i++,graphValue.getCount()));
            xAxisLabel.add(graphValue.getKey());
        }
        BarChart hourChart =  activityPathBinding.chart6;
        hourChart.setOnChartValueSelectedListener(PathActivity.this);
        hourChart.setDrawBarShadow(false);
        hourChart.setDrawValueAboveBar(true);
        hourChart.getDescription().setEnabled(false);
        hourChart.setMaxVisibleValueCount(60);
        hourChart.setPinchZoom(false);
        hourChart.setDrawGridBackground(false);
        hourChart.getAxisRight().setEnabled(false);
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                return xAxisLabel.get((int) value);
            }
        };

        XAxis xAxis = hourChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(24);
        xAxis.setValueFormatter(xAxisFormatter);
        YAxis leftAxis = hourChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);
        Legend l = hourChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        BarDataSet set1;
        if (hourChart.getData() != null &&
                hourChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) hourChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            hourChart.getData().notifyDataChanged();
            hourChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Past one year in hours");
            set1.setColors(ColorTemplate.JOYFUL_COLORS);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new MyValueFormatter());
            hourChart.setData(data);
        }
    }
    private void setHourinmonth(List<GraphValue> hourinmonth) {
        ArrayList<BarEntry> values = new ArrayList<>();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        int i=0;
        for(GraphValue graphValue : hourinmonth){
            values.add(new BarEntry(i++,graphValue.getCount()));
            xAxisLabel.add(graphValue.getKey());
        }
        BarChart hourChart =  activityPathBinding.chart4;
        hourChart.setOnChartValueSelectedListener(PathActivity.this);
        hourChart.setDrawBarShadow(false);
        hourChart.setDrawValueAboveBar(true);
        hourChart.getDescription().setEnabled(false);
        hourChart.setMaxVisibleValueCount(60);
        hourChart.setPinchZoom(false);
        hourChart.setDrawGridBackground(false);
        hourChart.getAxisRight().setEnabled(false);
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                return xAxisLabel.get((int) value);
            }
        };

        XAxis xAxis = hourChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(24);
        xAxis.setValueFormatter(xAxisFormatter);
        YAxis leftAxis = hourChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);
        Legend l = hourChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        BarDataSet set1;
        if (hourChart.getData() != null &&
                hourChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) hourChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            hourChart.getData().notifyDataChanged();
            hourChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Past one month in hours");
            set1.setColors(ColorTemplate.JOYFUL_COLORS);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new MyValueFormatter());
            hourChart.setData(data);
        }
    }
    private void setYearChart(List<GraphValue> year) {
        ArrayList<BarEntry> values = new ArrayList<>();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        int i=0;
        for(GraphValue graphValue : year){
            values.add(new BarEntry(i++,graphValue.getCount()));
            xAxisLabel.add(graphValue.getKey());
        }
        BarChart hourChart =  activityPathBinding.chart5;
        hourChart.setOnChartValueSelectedListener(PathActivity.this);
        hourChart.setDrawBarShadow(false);
        hourChart.setDrawValueAboveBar(true);
        hourChart.getDescription().setEnabled(false);
        hourChart.setMaxVisibleValueCount(60);
        hourChart.setPinchZoom(false);
        hourChart.setDrawGridBackground(false);
        hourChart.getAxisRight().setEnabled(false);
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                return xAxisLabel.get((int) value);
            }
        };

        XAxis xAxis = hourChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(6);
        xAxis.setValueFormatter(xAxisFormatter);
        YAxis leftAxis = hourChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);
        Legend l = hourChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        BarDataSet set1;
        if (hourChart.getData() != null &&
                hourChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) hourChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            hourChart.getData().notifyDataChanged();
            hourChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Past one year");
            set1.setColors(ColorTemplate.JOYFUL_COLORS);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new MyValueFormatter());
            hourChart.setData(data);
        }
    }
    private void setTenYearChart(List<GraphValue> tenYear) {
        ArrayList<BarEntry> values = new ArrayList<>();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        int i=0;
        for(GraphValue graphValue : tenYear){
            values.add(new BarEntry(i++,graphValue.getCount()));
            xAxisLabel.add(graphValue.getKey());
        }
        BarChart hourChart =  activityPathBinding.chart7;
        hourChart.setOnChartValueSelectedListener(PathActivity.this);
        hourChart.setDrawBarShadow(false);
        hourChart.setDrawValueAboveBar(true);
        hourChart.getDescription().setEnabled(false);
        hourChart.setMaxVisibleValueCount(60);
        hourChart.setPinchZoom(false);
        hourChart.setDrawGridBackground(false);
        hourChart.getAxisRight().setEnabled(false);
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                return xAxisLabel.get((int) value);
            }
        };

        XAxis xAxis = hourChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(10);
        xAxis.setValueFormatter(xAxisFormatter);
        YAxis leftAxis = hourChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);
        Legend l = hourChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        BarDataSet set1;
        if (hourChart.getData() != null &&
                hourChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) hourChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            hourChart.getData().notifyDataChanged();
            hourChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Past ten year");
            set1.setColors(ColorTemplate.JOYFUL_COLORS);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new MyValueFormatter());
            hourChart.setData(data);
        }
    }
    private void set24HourChart(List<GraphValue> hour) {
        ArrayList<BarEntry> values = new ArrayList<>();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        int i=0;
        for(GraphValue graphValue : hour){
            values.add(new BarEntry(i++,graphValue.getCount()));
            xAxisLabel.add(graphValue.getKey());
        }
        BarChart hourChart =  activityPathBinding.chart1;
        hourChart.setOnChartValueSelectedListener(PathActivity.this);
        hourChart.setDrawBarShadow(false);
        hourChart.setDrawValueAboveBar(true);
        hourChart.getDescription().setEnabled(false);
        hourChart.setMaxVisibleValueCount(60);
        hourChart.setPinchZoom(false);
        hourChart.setDrawGridBackground(false);
        hourChart.getAxisRight().setEnabled(false);
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                return xAxisLabel.get((int) value);
            }
        };

        XAxis xAxis = hourChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(24);
        xAxis.setValueFormatter(xAxisFormatter);
        YAxis leftAxis = hourChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);
        Legend l = hourChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        
        BarDataSet set1;
        if (hourChart.getData() != null &&
                hourChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) hourChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            hourChart.getData().notifyDataChanged();
            hourChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Past 24 Hours");
            set1.setColors(ColorTemplate.JOYFUL_COLORS);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new MyValueFormatter());
            hourChart.setData(data);
        }
    }

    private void setOneWeekChart(List<GraphValue> hour) {
        ArrayList<BarEntry> values = new ArrayList<>();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        int i=0;
        for(GraphValue graphValue : hour){
            values.add(new BarEntry(i++,graphValue.getCount()));
            xAxisLabel.add(graphValue.getKey());
        }
        BarChart hourChart =  activityPathBinding.chart2;
        hourChart.setOnChartValueSelectedListener(PathActivity.this);
        hourChart.setDrawBarShadow(false);
        hourChart.setDrawValueAboveBar(true);
        hourChart.getDescription().setEnabled(false);
        hourChart.setMaxVisibleValueCount(60);
        hourChart.setPinchZoom(false);
        hourChart.setDrawGridBackground(false);
        hourChart.getAxisRight().setEnabled(false);
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                return xAxisLabel.get((int) value);
            }
        };

        XAxis xAxis = hourChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);
        YAxis leftAxis = hourChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(false);
        Legend l = hourChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        BarDataSet set1;
        if (hourChart.getData() != null &&
                hourChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) hourChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            hourChart.getData().notifyDataChanged();
            hourChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Past one Week");
            set1.setColors(ColorTemplate.JOYFUL_COLORS);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new MyValueFormatter());
            hourChart.setData(data);
        }

    }
    private void setFourWeekChart(List<GraphValue> hour) {
        ArrayList<BarEntry> values = new ArrayList<>();
        final ArrayList<String> xAxisLabel = new ArrayList<>();
        int i=0;
        for(GraphValue graphValue : hour){
            values.add(new BarEntry(i++,graphValue.getCount()));
            xAxisLabel.add(graphValue.getKey());
        }
        HorizontalBarChart hourChart =  activityPathBinding.chart3;
        hourChart.setOnChartValueSelectedListener(PathActivity.this);
        hourChart.setDrawBarShadow(false);
        hourChart.setDrawValueAboveBar(true);
        hourChart.getDescription().setEnabled(false);
        hourChart.setMaxVisibleValueCount(60);
        hourChart.setPinchZoom(false);
        hourChart.setDrawGridBackground(false);
        hourChart.getAxisLeft().setEnabled(false);
        ValueFormatter xAxisFormatter = new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {

                return xAxisLabel.get((int) value);
            }
        };

        XAxis xAxis = hourChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(7);
        xAxis.setValueFormatter(xAxisFormatter);
        YAxis axisRight = hourChart.getAxisRight();
        axisRight.setAxisMinimum(0f);
        axisRight.setDrawGridLines(false);
        Legend l = hourChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);
        BarDataSet set1;
        if (hourChart.getData() != null &&
                hourChart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) hourChart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            hourChart.getData().notifyDataChanged();
            hourChart.notifyDataSetChanged();
        } else {
            set1 = new BarDataSet(values, "Past four Week");
            set1.setColors(ColorTemplate.JOYFUL_COLORS);
            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            dataSets.add(set1);
            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setBarWidth(0.9f);
            data.setValueFormatter(new MyValueFormatter());
            hourChart.setData(data);
        }
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        showSnack(isConnected, activityPathBinding.parent);
        setIntial(false);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mClusterManager = new ClusterManager<>(this, mMap);
        polylineModels = new ArrayList<>();
        marker = new ArrayList<MarkerArrayPath>();
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
                String streetname = cyclePathNew.getProperties().getStreetName();
                if (cyclePathNew.getProperties().getStreetName() == null || cyclePathNew.getProperties().getStreetName().isEmpty()) {
                    streetname = "No Street Name";
                }
                marker.add(new MarkerArrayPath(first,streetname));
                marker.add(new MarkerArrayPath(last,streetname));
                //mClusterManager.addItem(new MarkerArray(first, streetname));
                //mClusterManager.addItem(new MarkerArray(last, streetname));
                List<PatternItem> pattern = Arrays.asList(new Dot(), new Gap(15f));
                polylineOptions = new PolylineOptions()
                        .addAll(latLngs)
                        .color(cyclePathNew.getColor())
                        .width(10f)
                        .pattern(pattern);
                polylineModels.add(new PolylineModel(polylineOptions, cyclePathNew));
            }
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.runFinalization();
                Runtime.getRuntime().gc();
                System.gc();
                for (PolylineModel polylineModel : polylineModels) {
                    Polyline polyline = mMap.addPolyline(polylineModel.getPolylineOptions());
                    polyline.setClickable(true);
                }
                setupMap(marker);
            }
        };
        handler.postDelayed(runnable, 10);

    }

    private  void setupMap(ArrayList<MarkerArrayPath> marker) {
        for(MarkerArrayPath markerArray: marker) {
            mMap.addMarker(new MarkerOptions().position(markerArray.getPosition()).
                    flat(true).
                    title(markerArray.getTitle()).
                    icon(bitmapDescriptorFromVector(PathActivity.this, R.drawable.cust_marker)));
        }
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        builder.include(marker.get(0).getPosition());
        builder.include(marker.get(marker.size()-1).getPosition());
        LatLngBounds bounds = builder.build();
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mMap.animateCamera(cu);
       /* VenueMarkerRender renderer = new VenueMarkerRender(this,mMap,mClusterManager);
        mClusterManager.setRenderer(renderer);
        mMap.setOnCameraIdleListener(mClusterManager);
        mClusterManager.setAnimation(false);
        mMap.setOnMarkerClickListener(mClusterManager);
        mMap.setOnInfoWindowClickListener(mClusterManager);*/
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {

    }

    @Override
    public void onNothingSelected() {

    }
   /* public class VenueMarkerRender extends DefaultClusterRenderer<MarkerArray> {

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

    }*/

/*    private class MyTask extends AsyncTask<String, Void, String> {
        private ProgressDialog mProgressDialog;
        Runnable runnable;
        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            mProgressDialog = ProgressDialog.show(PathActivity.this, "Loading", "Loading Data ...");
            mProgressDialog.setCanceledOnTouchOutside(false); // main method that force user cannot click outside
            mProgressDialog.setCancelable(true);
            mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dlg) {
                    myTask.cancel(true);
                    handler.removeCallbacks(runnable);
                }
            });
        }
        @Override
        protected String doInBackground(String... params) {
                ArrayList<LatLng> latLngs = new ArrayList<>();
                for (int i = 0; i < cyclePathNew.getGeometry().getLatitudes().size(); i++) {
                    for (int j = 0; j < cyclePathNew.getGeometry().getLatitudes().get(i).size(); j++) {
                        Double lat = cyclePathNew.getGeometry().getLatitudes().get(i).get(j);
                        Double lon = cyclePathNew.getGeometry().getLongitudes().get(i).get(j);
                        LatLng latLng = new LatLng(lat, lon);
                        latLngs.add(latLng);
                    }
                    PolylineOptions polylineOptions = new PolylineOptions()
                            .addAll(latLngs)
                            .color(cyclePathNew.getColor())
                            .width(10f);
                    LatLng first = latLngs.get(0);
                    LatLng last = latLngs.get(latLngs.size() - 1);

                    //  polylineOptions1.add(polylineOptions);
                    runnable = new Runnable() {
                        @Override
                        public void run() {
                            System.runFinalization();
                            Runtime.getRuntime().gc();
                            System.gc();
                            String streetname = cyclePathNew.getProperties().getStreetName();
                            if (cyclePathNew.getProperties().getStreetName() == null || cyclePathNew.getProperties().getStreetName().isEmpty()) {
                                streetname = "No Street Name";
                            }
                            mMap.addMarker(new MarkerOptions().position(first).
                                    flat(true).
                                    title(streetname).
                                    icon(bitmapDescriptorFromVector(PathActivity.this, R.drawable.cust_marker)));
                            Polyline polyline = mMap.addPolyline(polylineOptions);
                            polyline.setClickable(true);
                            mMap.addMarker(new MarkerOptions().position(last).
                                    flat(true).
                                    title(streetname).
                                    icon(bitmapDescriptorFromVector(PathActivity.this, R.drawable.cust_marker)));
                            LatLngBounds.Builder builder = new LatLngBounds.Builder();
                            builder.include(first);
                            builder.include(last);
                            LatLngBounds bounds = builder.build();

                            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
                            mMap.animateCamera(cu);
                        }
                    };
                    handler.postDelayed(runnable, 10);
                }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (this.isCancelled()) {
                result = null;
                return;
            }

            if (mProgressDialog != null) {
                mProgressDialog.dismiss();
            }
        }
    }*/
    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
