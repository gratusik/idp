
package com.gratus.idp.model.common;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;


@SuppressWarnings("unused")
public class Properties implements Serializable {

    @SerializedName("city")
    private String mCity;
    @SerializedName("ID")
    private Long mID;
    @SerializedName("length")
    private Double mLength;
    @SerializedName("street_name")
    private String mStreetName;

    public String getCity() {
        return mCity;
    }

    public void setCity(String city) {
        mCity = city;
    }

    public Long getID() {
        return mID;
    }

    public void setID(Long iD) {
        mID = iD;
    }

    public Double getLength() {
        return mLength;
    }

    public void setLength(Double length) {
        mLength = length;
    }

    public String getStreetName() {
        return mStreetName;
    }

    public void setStreetName(String streetName) {
        mStreetName = streetName;
    }

}
