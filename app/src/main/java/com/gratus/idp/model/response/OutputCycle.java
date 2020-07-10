package com.gratus.idp.model.response;

public class OutputCycle {
    private String cyclepath_id;
    private int count;

    public OutputCycle() {
        // TODO Auto-generated constructor stub
    }

    public OutputCycle(String cyclepath_id, int count) {
        super();
        this.cyclepath_id = cyclepath_id;
        this.count = count;
    }

    public String getCyclepath_id() {
        return cyclepath_id;
    }

    public void setCyclepath_id(String cyclepath_id) {
        this.cyclepath_id = cyclepath_id;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
