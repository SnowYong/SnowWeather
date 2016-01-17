package com.snow.app.snowweather.util;

import android.text.TextUtils;

import com.snow.app.snowweather.db.SnowWeatherDB;
import com.snow.app.snowweather.model.City;
import com.snow.app.snowweather.model.County;
import com.snow.app.snowweather.model.Province;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;

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

}
