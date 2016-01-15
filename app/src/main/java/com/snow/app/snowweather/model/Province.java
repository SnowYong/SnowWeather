package com.snow.app.snowweather.model;

/**
 * Created by Administrator on 2016.01.15.
 */
public class Province {
    private int id;
    private String province_name;
    private String province_code;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvince_name() {
        return province_name;
    }

    public void setProvince_name(String province_name) {
        this.province_name = province_name;
    }

    public String getProvince_code() {
        return province_code;
    }

    public void setProvince_code(String province_code) {
        this.province_code = province_code;
    }
}
