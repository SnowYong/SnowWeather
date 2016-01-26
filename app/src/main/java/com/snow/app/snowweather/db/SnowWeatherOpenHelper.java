package com.snow.app.snowweather.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016.01.15.
 */
public class SnowWeatherOpenHelper extends SQLiteOpenHelper {
    private static final String CREATE_PROVINCE = "create table Province ("
            + "id integer primary key autoincrement,"
            + "province_name text,"
            + "province_code text)";

    private static final String CREATE_CITY = "create table City ("
            + "id integer primary key autoincrement,"
            + "city_name text,"
            + "city_code text,"
            + "province_id integer)";

    private static final String CREATE_COUNTY = "create table County ("
            + "id integer primary key autoincrement,"
            + "county_name text,"
            + "county_code text,"
            + "city_id integer)";

    private static final String CREATE_WEATHERINFO = "create table Weather ("
            + "id integer primary key autoincrement,"
            + "weather_cityName text,"
            + "weather_temp1 text,"
            + "weather_temp2 text,"
            + "weather_tempNow text,"
            + "weather_date text,"
            + "weather_publishTime text,"
            + "weather_description text,"
            + "weather_humidity text,"
            + "weather_windPower text,"
            + "weather_windDir text,"
            + "weather_citySelected integer,"
            + "weather_code text,"
            + "weather_url text)";

    public SnowWeatherOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROVINCE);
        db.execSQL(CREATE_CITY);
        db.execSQL(CREATE_COUNTY);
        db.execSQL(CREATE_WEATHERINFO);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        switch (oldVersion) {
            case 1:
            case 2:
            default:
        }
    }
}
