package com.snow.app.snowweather.model;

/**
 * Created by Administrator on 2016.01.26.
 */
public class Weather {
    private int id;
    private String city_name;
    private String weather_code;
    private String weather_url;
    private String weather_temp1;
    private String weather_temp2;
    private String weather_temnow;
    private String weather_desp;
    private String humidity;
    private String windPower;
    private String windDir;
    private String weather_ptime;
    private String weather_current_date;
    private int city_selected;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity_name() {
        return city_name;
    }

    public void setCity_name(String city_name) {
        this.city_name = city_name;
    }

    public String getWeather_code() {
        return weather_code;
    }

    public void setWeather_code(String weather_code) {
        this.weather_code = weather_code;
    }

    public String getWeather_url() {
        return weather_url;
    }

    public void setWeather_url(String weather_url) {
        this.weather_url = weather_url;
    }

    public String getWeather_temp1() {
        return weather_temp1;
    }

    public void setWeather_temp1(String weather_temp1) {
        this.weather_temp1 = weather_temp1;
    }

    public String getWeather_temp2() {
        return weather_temp2;
    }

    public void setWeather_temp2(String weather_temp2) {
        this.weather_temp2 = weather_temp2;
    }

    public String getWeather_temnow() {
        return weather_temnow;
    }

    public void setWeather_temnow(String weather_temnow) {
        this.weather_temnow = weather_temnow;
    }

    public String getWeather_desp() {
        return weather_desp;
    }

    public void setWeather_desp(String weather_desp) {
        this.weather_desp = weather_desp;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getWindPower() {
        return windPower;
    }

    public void setWindPower(String windPower) {
        this.windPower = windPower;
    }

    public String getWindDir() {
        return windDir;
    }

    public void setWindDir(String windDir) {
        this.windDir = windDir;
    }

    public String getWeather_ptime() {
        return weather_ptime;
    }

    public void setWeather_ptime(String weather_ptime) {
        this.weather_ptime = weather_ptime;
    }

    public String getWeather_current_date() {
        return weather_current_date;
    }

    public void setWeather_current_date(String weather_current_date) {
        this.weather_current_date = weather_current_date;
    }

    public int getCity_selected() {
        return city_selected;
    }

    public void setCity_selected(int city_selected) {
        this.city_selected = city_selected;
    }

}
