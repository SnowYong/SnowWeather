package com.snow.app.snowweather.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.snow.app.snowweather.model.City;
import com.snow.app.snowweather.model.County;
import com.snow.app.snowweather.model.Province;
import com.snow.app.snowweather.model.Weather;

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
        Cursor cursor = sqLiteDatabase.query("City", null, "province_id = ?",
                new String[]{String.valueOf(province_id)}, null, null, null);

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
        Cursor cursor = sqLiteDatabase.query("County", null, "city_id = ?",
                new String[]{String.valueOf(city_id)}, null, null, null);

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

    public void saveWeather(Weather weather) {
        if (weather != null) {
            ContentValues contentValues = new ContentValues();
            contentValues.put("weather_cityName", weather.getCity_name());
            contentValues.put("weather_temp1", weather.getWeather_temp1());
            contentValues.put("weather_temp2", weather.getWeather_temp2());
            contentValues.put("weather_tempNow", weather.getWeather_temnow());
            contentValues.put("weather_date", weather.getWeather_current_date());
            contentValues.put("weather_dayofweek", weather.getWeather_current_dayofweek());
            contentValues.put("weather_publishTime", weather.getWeather_ptime());
            contentValues.put("weather_description", weather.getWeather_desp());
            contentValues.put("weather_humidity", weather.getHumidity());
            contentValues.put("weather_windPower", weather.getWindPower());
            contentValues.put("weather_windDir", weather.getWindDir());
            contentValues.put("weather_citySelected", weather.getCity_selected());
            contentValues.put("weather_code", weather.getWeather_code());
            contentValues.put("weather_url", weather.getWeather_url());
            sqLiteDatabase.insert("Weather", null, contentValues);
        }
    }

    public List<Weather> loadWeather(String weatherCode, String weatherUrl) {
        List<Weather> weatherList = new ArrayList<Weather>();

        Cursor cursor = sqLiteDatabase.query("Weather", null, "weather_code = ? and weather_url = ?",
                new String[]{weatherCode, weatherUrl}, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Weather weather = new Weather();
                weather.setId(cursor.getInt(cursor.getColumnIndex("id")));
                weather.setCity_name(cursor.getString(cursor.getColumnIndex("weather_cityName")));
                weather.setWeather_temp1(cursor.getString(cursor.getColumnIndex("weather_temp1")));
                weather.setWeather_temp2(cursor.getString(cursor.getColumnIndex("weather_temp2")));
                weather.setWeather_temnow(cursor.getString(cursor.getColumnIndex("weather_tempNow")));
                weather.setWeather_current_date(cursor.getString(cursor.getColumnIndex("weather_date")));
                weather.setWeather_current_dayofweek(cursor.getInt(cursor.getColumnIndex("weather_dayofweek")));
                weather.setWeather_ptime(cursor.getString(cursor.getColumnIndex("weather_publishTime")));
                weather.setWeather_desp(cursor.getString(cursor.getColumnIndex("weather_description")));
                weather.setHumidity(cursor.getString(cursor.getColumnIndex("weather_humidity")));
                weather.setWindPower(cursor.getString(cursor.getColumnIndex("weather_windPower")));
                weather.setWindDir(cursor.getString(cursor.getColumnIndex("weather_windDir")));
                weather.setCity_selected(cursor.getInt(cursor.getColumnIndex("weather_citySelected")));
                weather.setWeather_code(cursor.getString(cursor.getColumnIndex("weather_code")));
                weather.setWeather_url(cursor.getString(cursor.getColumnIndex("weather_url")));

                weatherList.add(weather);
            }
            while (cursor.moveToNext());
        }

        return weatherList;
    }
}
