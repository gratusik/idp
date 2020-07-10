package com.gratus.idp.model.common;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

public class MarkerArrayPath implements Serializable {
    private LatLng position;
    private String streetName;

    public MarkerArrayPath() {
    }

    public MarkerArrayPath(LatLng position, String streetName) {
        this.position = position;
        this.streetName = streetName;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }


    public LatLng getPosition() {
        return position;
    }


    public String getTitle() {
        return streetName;
    }

    public String getSnippet() {
        return null;
    }
}
