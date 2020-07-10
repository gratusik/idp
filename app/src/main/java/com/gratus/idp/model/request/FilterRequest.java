package com.gratus.idp.model.request;

import androidx.databinding.BaseObservable;

public class FilterRequest extends BaseObservable {
    private String startdate;
    private String enddate;
    private String starthour;
    private String endhour;
    private boolean hourwise,datewise;
    private boolean visibilityProgressBar,visibilityHour,visibilityDate;

    public FilterRequest() {
    }

    public FilterRequest(String startdate, String enddate, String starthour, String endhour, boolean hourwise) {
        this.startdate = startdate;
        this.enddate = enddate;
        this.starthour = starthour;
        this.endhour = endhour;
        this.hourwise = hourwise;
    }

    public boolean isDatewise() {
        return datewise;
    }

    public void setDatewise(boolean datewise) {
        this.datewise = datewise;
        notifyChange();
    }

    public String getStartdate() {
        return startdate;
    }

    public void setStartdate(String startdate) {
        this.startdate = startdate;
    }

    public String getEnddate() {
        return enddate;
    }

    public void setEnddate(String enddate) {
        this.enddate = enddate;
    }

    public String getStarthour() {
        return starthour;
    }

    public void setStarthour(String starthour) {
        this.starthour = starthour;
    }

    public String getEndhour() {
        return endhour;
    }

    public void setEndhour(String endhour) {
        this.endhour = endhour;
    }

    public boolean isHourwise() {
        return hourwise;
    }

    public void setHourwise(boolean hourwise) {
        this.hourwise = hourwise;
        notifyChange();
    }

    public boolean isVisibilityProgressBar() {
        return visibilityProgressBar;
    }

    public void setVisibilityProgressBar(boolean visibilityProgressBar) {
        this.visibilityProgressBar = visibilityProgressBar;
        notifyChange();
    }

    public boolean isVisibilityHour() {
        return visibilityHour;
    }

    public void setVisibilityHour(boolean visibilityHour) {
        this.visibilityHour = visibilityHour;
        notifyChange();
    }

    public boolean isVisibilityDate() {
        return visibilityDate;

    }

    public void setVisibilityDate(boolean visibilityDate) {
        this.visibilityDate = visibilityDate;
        notifyChange();
    }
}
