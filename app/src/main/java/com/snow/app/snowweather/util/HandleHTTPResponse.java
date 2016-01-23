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
                                String qu_name = xmlPullParser.getAttributeValue(null, "quName");
                                String py_name = xmlPullParser.getAttributeValue(null, "pyName");
                                if (!py_name.equals("xisha") && !py_name.equals("nanshadao")
                                        && !py_name.equals("diaoyudao")) {
                                    Province province = new Province();
                                    province.setProvince_name(qu_name);
                                    province.setProvince_code(py_name);
                                    snowWeatherDB.saveProvince(province);
                                }
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

    public static boolean handleCityResponse(String response, int province_id, SnowWeatherDB snowWeatherDB, boolean extra_click) {
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
                                if (extra_click) {
                                    city.setCity_code(xmlPullParser.getAttributeValue(null, "url"));
                                } else {
                                    city.setCity_code(xmlPullParser.getAttributeValue(null, "pyName"));
                                }
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

//    public static boolean handleWeatherResponse(Context context, String response) {
//        try {
//            if (!TextUtils.isEmpty(response)) {
//                JSONObject jsonObject = new JSONObject(response);
//                JSONObject weatherJson = jsonObject.getJSONObject("weatherinfo");
//                String city_name = weatherJson.getString("city");
//                String weather_code = weatherJson.getString("cityid");
//                String weather_temp1 = weatherJson.getString("temp1");
//                String weather_temp2 = weatherJson.getString("temp2");
//                String weather_desp = weatherJson.getString("weather");
//                String weather_ptime = weatherJson.getString("ptime");
//                saveWeatherInfo(context, city_name, weather_code, weather_temp1, weather_temp2,
//                        weather_desp, weather_ptime);
//                return true;
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            return false;
//        }
//        return false;
//    }

//    private static void saveWeatherInfo(Context context, String city_name, String weather_code, String weather_temp1, String weather_temp2, String weather_desp, String weather_ptime) {
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
//        SharedPreferences.Editor editor = PreferenceManager
//                .getDefaultSharedPreferences(context).edit();
//        editor.putString("city_name", city_name);
//        editor.putString("weather_code", weather_code);
//        editor.putString("weather_temp1", weather_temp1);
//        editor.putString("weather_temp2", weather_temp2);
//        editor.putString("weather_desp", weather_desp);
//        editor.putString("weather_ptime", weather_ptime);
//        editor.putString("weather_current_data", simpleDateFormat.format(new Date()));
//        editor.putBoolean("city_selected", true);
//        editor.commit();
//    }


    public static void handleWeatherResponse(Context context, String response, String url) {
        try {
            if (!TextUtils.isEmpty(response)) {
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                XmlPullParser xmlPullParser = factory.newPullParser();
                xmlPullParser.setInput(new StringReader(response));
                int event_type = xmlPullParser.getEventType();
                String weather_code_init = xmlPullParser.getName();

                while (event_type != XmlPullParser.END_DOCUMENT) {
                    String node_name = xmlPullParser.getName();
                    switch (event_type) {
                        case XmlPullParser.START_TAG: {
                            if (node_name.equals("city")) {
                                if (xmlPullParser.getAttributeValue(null, "url").equals(url)) {
                                    String city_name = xmlPullParser.getAttributeValue(null, "cityname");
                                    String weather_code = weather_code_init;
                                    String county_url = url;
                                    String weather_temp1 = xmlPullParser.getAttributeValue(null, "tem1");
                                    String weather_temp2 = xmlPullParser.getAttributeValue(null, "tem2");
                                    String weather_temnow = xmlPullParser.getAttributeValue(null, "temNow");
                                    String weather_desp = xmlPullParser.getAttributeValue(null, "stateDetailed");
                                    String humidity = xmlPullParser.getAttributeValue(null, "humidity");
                                    String windPower = xmlPullParser.getAttributeValue(null, "windPower");
                                    String windDir = xmlPullParser.getAttributeValue(null, "windDir");
                                    String weather_ptime = xmlPullParser.getAttributeValue(null, "time");
                                    saveWeatherInfo(context, city_name, weather_code, county_url, weather_temp1, weather_temp2,
                                            weather_temnow, weather_desp, humidity, windPower, windDir, weather_ptime);
                                }
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void saveWeatherInfo(Context context, String city_name, String weather_code, String county_url, String weather_temp1, String weather_temp2,
                                        String weather_temnow, String weather_desp, String humidity, String windPower, String windDir, String weather_ptime) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月d日", Locale.CHINA);
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putString("city_name", city_name);
        editor.putString("weather_code", weather_code);
        editor.putString("county_url", county_url);
        editor.putString("weather_temp1", weather_temp1);
        editor.putString("weather_temp2", weather_temp2);
        editor.putString("weather_tempnow", weather_temnow);
        editor.putString("weather_desp", weather_desp);
        editor.putString("humidity", humidity);
        editor.putString("windPower", windPower);
        editor.putString("windDir", windDir);
        editor.putString("weather_ptime", weather_ptime);
        editor.putString("weather_current_date", simpleDateFormat.format(new Date()));
        editor.putBoolean("city_selected", true);
        editor.commit();
    }
}
