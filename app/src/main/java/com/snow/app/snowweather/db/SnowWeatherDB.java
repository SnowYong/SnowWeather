package com.snow.app.snowweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.snow.app.snowweather.model.City;
import com.snow.app.snowweather.model.County;
import com.snow.app.snowweather.model.Province;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016.01.15.
 */
public class SnowWeatherDB {
    private static final String DB_NAME = "SnowWeather";
    private static final int DB_VERSION = 1;

    private static SnowWeatherDB snowWeatherDB;
    private SQLiteDatabase sqLiteDatabase;

    private SnowWeatherDB(Context context) {
        SnowWeatherOpenHelper snowWeatherOpenHelper = new SnowWeatherOpenHelper(context,
                DB_NAME, null, DB_VERSION);
        sqLiteDatabase = snowWeatherOpenHelper.getWritableDatabase();
    }

    public synchronized static SnowWeatherDB getInstance(Context context) {
        if (snowWeatherDB == null) {
            snowWeatherDB = new SnowWeatherDB(context);
        }
        return snowWeatherDB;
    }

    public void saveProvince(Province province) {
        if (province != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("province_name", province.getProvince_name());
            contentValues.put("province_code", province.getProvince_code());
            sqLiteDatabase.insert("Province", null, contentValues);
        }
    }

    public List<Province> loadProvince() {
        List<Province> provinceList = new ArrayList<Province>();
        Cursor cursor = sqLiteDatabase.query("Province", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Province province = new Province();
                province.setId(cursor.getInt(cursor.getColumnIndex("id")));
                province.setProvince_name(cursor.getString(cursor.getColumnIndex("province_name")));
                province.setProvince_code(cursor.getString(cursor.getColumnIndex("province_code")));
                provinceList.add(province);
            }
            while (cursor.moveToNext());
        }

        return provinceList;
    }

    public void saveCity(City city) {
        if (city != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("city_name", city.getCity_name());
            contentValues.put("city_code", city.getCity_code());
            contentValues.put("province_id", city.getProvince_id());
            sqLiteDatabase.insert("City", null, contentValues);
        }
    }

    public List<City> loadCity(int province_id) {
        List<City> cityList = new ArrayList<City>();
        Cursor cursor = sqLiteDatabase.query("City", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                City city = new City();
                city.setId(cursor.getInt(cursor.getColumnIndex("id")));
                city.setCity_name(cursor.getString(cursor.getColumnIndex("city_name")));
                city.setCity_code(cursor.getString(cursor.getColumnIndex("city_code")));
                city.setProvince_id(province_id);
                cityList.add(city);
            }
            while (cursor.moveToNext());
        }

        return cityList;
    }

    public void cleanCity()
    {
        sqLiteDatabase.delete("City", null, null);
    }

    public void saveCounty(County county) {
        if (county != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("county_name", county.getCounty_name());
            contentValues.put("county_code", county.getCounty_code());
            contentValues.put("city_id", county.getCity_id());
            sqLiteDatabase.insert("County", null, contentValues);
        }
    }

    public List<County> loadCounty(int city_id) {
        List<County> countyList = new ArrayList<County>();
        Cursor cursor = sqLiteDatabase.query("County", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                County county = new County();
                county.setId(cursor.getInt(cursor.getColumnIndex("id")));
                county.setCounty_name(cursor.getString(cursor.getColumnIndex("county_name")));
                county.setCounty_code(cursor.getString(cursor.getColumnIndex("county_code")));
                county.setCity_id(city_id);
                countyList.add(county);
            }
            while (cursor.moveToNext());
        }

        return countyList;
    }

    public void cleanCounty()
    {
        sqLiteDatabase.delete("County", null, null);
    }
}
