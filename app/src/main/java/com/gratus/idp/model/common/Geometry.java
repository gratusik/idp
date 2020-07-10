
package com.gratus.idp.model.common;

import java.io.Serializable;
import java.util.List;

import com.google.gson.annotations.SerializedName;


@SuppressWarnings("unused")
public class Geometry  implements Serializable {

    @SerializedName("latitudes")
    private List<List<Double>> mLatitudes;
    @SerializedName("longitudes")
    private List<List<Double>> mLongitudes;
    @SerializedName("type")
    private String mType;

    public List<List<Double>> getLatitudes() {
        return mLatitudes;
    }

    public void setLatitudes(List<List<Double>> latitudes) {
        mLatitudes = latitudes;
    }

    public List<List<Double>> getLongitudes() {
        return mLongitudes;
    }

    public void setLongitudes(List<List<Double>> longitudes) {
        mLongitudes = longitudes;
    }

    public String getType() {
        return mType;
    }

    public void setType(String type) {
        mType = type;
    }

}
