package com.gratus.idp.model.common;

import java.io.Serializable;

public class Id implements Serializable {
    private String $oid;

    public Id() {
    }

    public String get$oid() {
        return $oid;
    }

    public void set$oid(String $oid) {
        this.$oid = $oid;
    }
}
