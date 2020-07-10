package com.gratus.idp.model.common;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MarkerArray implements ClusterItem {
    private LatLng position;
    private String streetName;


    public MarkerArray(LatLng position, String streetName) {
        this.position = position;
        this.streetName = streetName;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return streetName;
    }

    @Override
    public String getSnippet() {
        return null;
    }
}
