package com.snow.app.snowweather.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.snow.app.snowweather.db.SnowWeatherDB;
import com.snow.app.snowweather.model.City;
import com.snow.app.snowweather.model.County;
import com.snow.app.snowweather.model.Province;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Administrator on 2016.01.16.
 */
public class HandleHTTPResponse {

    public static boolean handleProvinceResponse(String response, SnowWeatherDB snowWeatherDB) {
        try {
            if (!TextUtils.isEmpty(response)) {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new StringReader(response));
                int event_type = xmlPullParser.getEventType();

                while (event_type != XmlPullParser.END_DOCUMENT) {
                    String node_name = xmlPullParser.getName();
                    switch (event_type) {
                        case XmlPullParser.START_TAG: {
                            if (node_name.equals("city")) {
                                Province province = new Province();
                                province.setProvince_name(xmlPullParser.getAttributeValue(null, "quName"));
                                province.setProvince_code(xmlPullParser.getAttributeValue(null, "pyName"));
                                snowWeatherDB.saveProvince(province);
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG: {
                            if (node_name.equals("china")) {

                            }
                            break;
                        }
                        default:
                            break;
                    }

                    event_type = xmlPullParser.next();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public static boolean handleCityResponse(String response, int province_id, SnowWeatherDB snowWeatherDB) {
        try {
            if (!TextUtils.isEmpty(response)) {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new StringReader(response));
                int event_type = xmlPullParser.getEventType();

                while (event_type != XmlPullParser.END_DOCUMENT) {
                    String node_name = xmlPullParser.getName();
                    switch (event_type) {
                        case XmlPullParser.START_TAG: {
                            if (node_name.equals("city")) {
                                City city = new City();
                                city.setCity_name(xmlPullParser.getAttributeValue(null, "cityname"));
                                city.setCity_code(xmlPullParser.getAttributeValue(null, "pyName"));
                                city.setProvince_id(province_id);
                                snowWeatherDB.saveCity(city);
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG: {
                            if (node_name.equals("china")) {

                            }
                            break;
                        }
                        default:
                            break;
                    }

                    event_type = xmlPullParser.next();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public static boolean handleCountyResponse(String response, int city_id, SnowWeatherDB snowWeatherDB) {
        try {
            if (!TextUtils.isEmpty(response)) {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new StringReader(response));
                int event_type = xmlPullParser.getEventType();

                while (event_type != XmlPullParser.END_DOCUMENT) {
                    String node_name = xmlPullParser.getName();
                    switch (event_type) {
                        case XmlPullParser.START_TAG: {
                            if (node_name.equals("city")) {
                                County county = new County();
                                county.setCounty_name(xmlPullParser.getAttributeValue(null, "cityname"));
                                county.setCounty_code(xmlPullParser.getAttributeValue(null, "url"));
                                county.setCity_id(city_id);
                                snowWeatherDB.saveCounty(county);
                            }
                            break;
                        }
                        case XmlPullParser.END_TAG: {
                            if (node_name.equals("china")) {

                            }
                            break;
                        }
                        default:
                            break;
                    }

                    event_type = xmlPullParser.next();
                }
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return false;
    }

    public static boolean handleWeatherResponse(Context context, String response) {
        try {
            if (!TextUtils.isEmpty(response)) {
                JSONObject jsonObject = new JSONObject(response);
                JSONObject weatherJson = jsonObject.getJSONObject("weatherinfo");
                String city_name = weatherJson.getString("city");
                String weather_code = weatherJson.getString("cityid");
                String weather_temp1 = weatherJson.getString("temp1");
                String weather_temp2 = weatherJson.getString("temp2");
                String weather_desp = weatherJson.getString("weather");
                String weather_ptime = weatherJson.getString("ptime");
                saveWeatherInfo(context, city_name, weather_code, weather_temp1, weather_temp2,
                        weather_desp, weather_ptime);
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }

    private static void saveWeatherInfo(Context context, String city_name, String weather_code, String weather_temp1, String weather_temp2, String weather_desp, String weather_ptime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putString("city_name", city_name);
        editor.putString("weather_code", weather_code);
        editor.putString("weather_temp1", weather_temp1);
        editor.putString("weather_temp2", weather_temp2);
        editor.putString("weather_desp", weather_desp);
        editor.putString("weather_ptime", weather_ptime);
        editor.putString("weather_current_data", simpleDateFormat.format(new Date()));
        editor.putBoolean("city_selected", true);
        editor.commit();
    }

}
