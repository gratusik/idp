package com.gratus.idp.model.common;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.Serializable;
import java.util.ArrayList;

public class PolylineModel implements Serializable {
    PolylineOptions polylineOptions;
    CyclePathNew cyclePathNew;

    public PolylineModel() {
    }

    public PolylineModel(PolylineOptions polylineOptions, CyclePathNew cyclePathNew) {
        this.polylineOptions = polylineOptions;
        this.cyclePathNew = cyclePathNew;
    }

    public PolylineOptions getPolylineOptions() {
        return polylineOptions;
    }

    public void setPolylineOptions(PolylineOptions polylineOptions) {
        this.polylineOptions = polylineOptions;
    }

    public CyclePathNew getCyclePathNew() {
        return cyclePathNew;
    }

    public void setCyclePathNew(CyclePathNew cyclePathNew) {
        this.cyclePathNew = cyclePathNew;
    }
}
