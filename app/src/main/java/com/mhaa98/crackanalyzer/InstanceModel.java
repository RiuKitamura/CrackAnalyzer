package com.mhaa98.crackanalyzer;

import com.google.gson.annotations.SerializedName;

public class InstanceModel {
    @SerializedName("nama_instance")
    private String nama_instance;

    public String getInstance() {
        return nama_instance;
    }
}