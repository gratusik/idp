package com.gratus.idp.model.request;

import androidx.databinding.BaseObservable;

public class PathRequest extends BaseObservable {
    private int pathid;


    public PathRequest() {
    }

    public PathRequest(int pathid) {
        this.pathid = pathid;
    }

    public int getPathid() {
        return pathid;
    }

    public void setPathid(int pathid) {
        this.pathid = pathid;
    }
}
