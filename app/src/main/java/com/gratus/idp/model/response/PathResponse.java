package com.gratus.idp.model.response;

import androidx.databinding.BaseObservable;

import java.io.Serializable;
import java.util.List;

public class PathResponse extends BaseObservable implements Serializable {
    private int status;
    private boolean success;
    private String message;
    private List<GraphValue> year;
    private List<GraphValue> tenyear;
    private List<GraphValue> onemonth;
    private List<GraphValue> oneweek;
    private List<GraphValue> fourweek;
    private List<GraphValue> hour;
    private List<GraphValue> hourinmonth;
    private List<GraphValue> hourinyear;
    public PathResponse() {
    }

    public PathResponse(int status, boolean success, String message) {
        this.status = status;
        this.success = success;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<GraphValue> getYear() {
        return year;
    }

    public void setYear(List<GraphValue> year) {
        this.year = year;
    }

    public List<GraphValue> getTenyear() {
        return tenyear;
    }

    public void setTenyear(List<GraphValue> tenyear) {
        this.tenyear = tenyear;
    }

    public List<GraphValue> getOnemonth() {
        return onemonth;
    }

    public void setOnemonth(List<GraphValue> onemonth) {
        this.onemonth = onemonth;
    }

    public List<GraphValue> getOneweek() {
        return oneweek;
    }

    public void setOneweek(List<GraphValue> oneweek) {
        this.oneweek = oneweek;
    }

    public List<GraphValue> getFourweek() {
        return fourweek;
    }

    public void setFourweek(List<GraphValue> fourweek) {
        this.fourweek = fourweek;
    }

    public List<GraphValue> getHour() {
        return hour;
    }

    public void setHour(List<GraphValue> hour) {
        this.hour = hour;
    }

    public List<GraphValue> getHourinmonth() {
        return hourinmonth;
    }

    public void setHourinmonth(List<GraphValue> hourinmonth) {
        this.hourinmonth = hourinmonth;
    }

    public List<GraphValue> getHourinyear() {
        return hourinyear;
    }

    public void setHourinyear(List<GraphValue> hourinyear) {
        this.hourinyear = hourinyear;
    }
}
