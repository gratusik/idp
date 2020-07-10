package com.gratus.idp.model.common;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.Comparator;

import de.undercouch.bson4jackson.types.ObjectId;

public class CyclePathNew implements Serializable,Comparable<CyclePathNew> {
    private Id _id;
   // private String _id;
    private String paths;
    private boolean existing;
    private String tag;
    private Properties properties;
    private Geometry geometry;
    private Integer count;
    private Drawable drawable;
    private int color;
    private int layoutId;

    public CyclePathNew() {
    }

    public CyclePathNew(Id _id, String paths, boolean existing, Properties properties, Geometry geometry, Integer count, int color) {
        this._id = _id;
        this.paths = paths;
        this.existing = existing;
        this.properties = properties;
        this.geometry = geometry;
        this.count = count;
        this.color = color;
    }

    public Id get_id() {
        return _id;
    }

    public void set_id(Id _id) {
        this._id = _id;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getPaths() {
        return paths;
    }

    public void setPaths(String paths) {
        this.paths = paths;
    }

    public boolean isExisting() {
        return existing;
    }

    public void setExisting(boolean existing) {
        this.existing = existing;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }

    public int getLayoutId() {
        return layoutId;
    }

    public void setLayoutId(int layoutId) {
        this.layoutId = layoutId;
    }

    public static class Comparators {

        public static Comparator<CyclePathNew> COUNT = new Comparator<CyclePathNew>() {
            @Override
            public int compare(CyclePathNew o1, CyclePathNew o2) {
                return o1.count - o2.count;
            }
        };
    }

    @Override
    public int compareTo(CyclePathNew o) {
        return this.getCount().compareTo(o.getCount());
    }
}
