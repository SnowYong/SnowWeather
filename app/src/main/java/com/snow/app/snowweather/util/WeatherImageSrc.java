package com.snow.app.snowweather.util;

import java.util.HashMap;

import com.snow.app.snowweather.R;

/**
 * Created by Administrator on 2016.01.29.
 */
public class WeatherImageSrc {
    private static int imgSrcResult;
    private static HashMap weatherMap;
    private static WeatherImageSrc weatherImageSrc;

    private WeatherImageSrc() {
        weatherMap = new HashMap();
        weatherMap.put("阵雨", R.drawable.rainy_while);
        weatherMap.put("小雨", R.drawable.light_rainy);
        weatherMap.put("小到中雨", R.drawable.middle_rainy);
        weatherMap.put("中雨", R.drawable.middle_rainy);
        weatherMap.put("中到大雨", R.drawable.heavy_rainy);
        weatherMap.put("大雨", R.drawable.heavy_rainy);
        weatherMap.put("大到暴雨", R.drawable.rain_storm);
        weatherMap.put("暴雨", R.drawable.rain_storm);
        weatherMap.put("阴", R.drawable.overcast);
        weatherMap.put("多云", R.drawable.cloudy);
        weatherMap.put("阵雪", R.drawable.snowy_while);
        weatherMap.put("小雪", R.drawable.light_snowy);
        weatherMap.put("雨夹雪", R.drawable.rainy_with_snowy);
        weatherMap.put("晴", R.drawable.sunny);
        weatherMap.put("雾", R.drawable.foggy);
        weatherMap.put("霾", R.drawable.dirty_foggy);
    }

    public static WeatherImageSrc getInstance() {
        if (weatherImageSrc == null) {
            weatherImageSrc = new WeatherImageSrc();
        }
        return weatherImageSrc;
    }

    public int getWeatherImgSrc(String weatherDesp) {
        imgSrcResult = (int) weatherMap.get(weatherDesp);
        return imgSrcResult;
    }
}
