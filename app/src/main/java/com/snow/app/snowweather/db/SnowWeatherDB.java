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
    private static SQLiteDatabase sqLiteDatabase;

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

    public static void saveProvince(Province province) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("province_name", province.getProvince_name());
        contentValues.put("province_code", province.getProvince_code());
        sqLiteDatabase.insert("Province", null, contentValues);
    }

    public static List<Province> loadProvince() {
        List<Province> provinceList = new ArrayList<Province>();
        Province province = new Province();
        Cursor cursor = sqLiteDatabase.query("Province", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String province_name = cursor.getString(cursor.getColumnIndex("province_name"));
                String province_code = cursor.getString(cursor.getColumnIndex("province_code"));
                province.setId(id);
                province.setProvince_name(province_name);
                province.setProvince_code(province_code);
                provinceList.add(province);
            }
            while (cursor.moveToNext());
        }

        return provinceList;
    }

    public static void saveCity(City city, int province_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("city_name", city.getCity_name());
        contentValues.put("city_code", city.getCity_code());
        contentValues.put("province_id", province_id);
        sqLiteDatabase.insert("City", null, contentValues);
    }

    public static List<City> loadCity() {
        List<City> cityList = new ArrayList<City>();
        City city = new City();
        Cursor cursor = sqLiteDatabase.query("City", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String city_name = cursor.getString(cursor.getColumnIndex("city_name"));
                String city_code = cursor.getString(cursor.getColumnIndex("city_code"));
                int province_id = cursor.getInt(cursor.getColumnIndex("province_id"));
                city.setId(id);
                city.setCity_name(city_name);
                city.setCity_code(city_code);
                city.setProvince_id(province_id);
                cityList.add(city);
            }
            while (cursor.moveToNext());
        }

        return cityList;
    }

    public static void saveCounty(County county, int city_id) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("county_name", county.getCounty_name());
        contentValues.put("county_code", county.getCounty_code());
        contentValues.put("city_id", city_id);
        sqLiteDatabase.insert("Province", null, contentValues);
    }

    public static List<County> loadCounty() {
        List<County> countyList = new ArrayList<County>();
        County county = new County();
        Cursor cursor = sqLiteDatabase.query("County", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndex("id"));
                String county_name = cursor.getString(cursor.getColumnIndex("county_name"));
                String county_code = cursor.getString(cursor.getColumnIndex("county_code"));
                int city_id = cursor.getInt(cursor.getColumnIndex("city_id"));
                county.setId(id);
                county.setCounty_name(county_name);
                county.setCounty_code(county_code);
                county.setCity_id(city_id);
                countyList.add(county);
            }
            while (cursor.moveToNext());
        }

        return countyList;
    }
}
