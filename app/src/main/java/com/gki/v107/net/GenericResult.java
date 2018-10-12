package com.gki.v107.net;

/**
 * Copyright 2018 bejson.com
 */

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Auto-generated: 2018-07-19 14:31:44
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
public class GenericResult<T> {

    @SerializedName("odata.metadata")
    private String odata_metadata;

    private List<T> value;

    public String getOdata_metadata() {
        return odata_metadata;
    }

    public void setOdata_metadata(String odata_metadata) {
        this.odata_metadata = odata_metadata;
    }

    public List<T> getValue() {
        return value;
    }

    public void setValue(List<T> value) {
        this.value = value;
    }

}
