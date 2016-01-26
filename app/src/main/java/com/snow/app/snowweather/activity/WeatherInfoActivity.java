package com.snow.app.snowweather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.snow.app.snowweather.R;
import com.snow.app.snowweather.db.SnowWeatherDB;
import com.snow.app.snowweather.model.Weather;
import com.snow.app.snowweather.service.UpdateWeatherInfoService;
import com.snow.app.snowweather.util.HTTPCallBackListener;
import com.snow.app.snowweather.util.HTTPUtil;
import com.snow.app.snowweather.util.HandleHTTPResponse;

import java.util.List;

/**
 * Created by Administrator on 2016.01.18.
 */
public class WeatherInfoActivity extends Activity implements View.OnClickListener {
    private LinearLayout weather_info_layout, weather_temp_vary_layout;
    private TextView weather_name_textview, weather_ptime_textview, weather_current_date,
            weather_desp_textview, weather_temp1_textview, weather_temp2_textview;
    private TextView weather_tempnow_textview, weather_extra_textview;
    private ImageButton weather_rechoose_btn, weather_update_btn, weather_setting_btn;
    private ImageView weather_ptime_imageview;

    private List<Weather> weatherList;
    private SnowWeatherDB snowWeatherDB;

    private Weather currentWeather;
    private String currentCode;
    private String currentUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_info_activity);

        widgetInit();

        snowWeatherDB = SnowWeatherDB.getInstance(this);
        currentCode = getIntent().getStringExtra("weather_code");
        currentUrl = getIntent().getStringExtra("weather_url");
        if (!TextUtils.isEmpty(currentCode) && !TextUtils.isEmpty(currentUrl)) {
            seekingWeatherInfoShow();
            queryWeather();
        } else {
            SharedPreferences sharedPreferences = PreferenceManager
                    .getDefaultSharedPreferences(this);
            currentCode = sharedPreferences.getString("weather_code", "");
            currentUrl = sharedPreferences.getString("weather_url", "");
            queryWeather();
        }

        serviceInit();
    }

    private void seekingWeatherInfoShow() {
        weather_ptime_imageview.setVisibility(View.INVISIBLE);
        weather_name_textview.setVisibility(View.INVISIBLE);
        weather_current_date.setVisibility(View.INVISIBLE);
        weather_info_layout.setVisibility(View.INVISIBLE);
        weather_temp_vary_layout.setVisibility(View.INVISIBLE);
        weather_ptime_textview.setText("正在同步中...");
    }

    private void widgetInit() {
        weather_name_textview = (TextView) findViewById(R.id.weather_name_textview);
        weather_ptime_textview = (TextView) findViewById(R.id.weather_ptime_textview);

        weather_info_layout = (LinearLayout) findViewById(R.id.weather_info_layout);
        weather_temp_vary_layout = (LinearLayout) findViewById(R.id.weather_temp_vary_layout);

        weather_current_date = (TextView) findViewById(R.id.weather_current_date);
        weather_ptime_imageview = (ImageView) findViewById(R.id.weather_ptime_imageview);

        weather_desp_textview = (TextView) findViewById(R.id.weather_desp_textview);
        weather_temp1_textview = (TextView) findViewById(R.id.weather_temp1_textview);
        weather_temp2_textview = (TextView) findViewById(R.id.weather_temp2_textview);
        weather_tempnow_textview = (TextView) findViewById(R.id.weather_tempnow_textview);
        weather_extra_textview = (TextView) findViewById(R.id.weather_extra_textview);

        weather_rechoose_btn = (ImageButton) findViewById(R.id.weather_rechoose_btn);
        weather_update_btn = (ImageButton) findViewById(R.id.weather_update_btn);
        weather_setting_btn = (ImageButton) findViewById(R.id.weather_setting_btn);
        weather_rechoose_btn.setOnClickListener(this);
        weather_update_btn.setOnClickListener(this);
        weather_setting_btn.setOnClickListener(this);
    }

    private void serviceInit() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        int updateTime = sharedPreferences.getInt("update_time", 1);
        boolean serviceFlag = sharedPreferences.getBoolean("service_flag", true);

        if (serviceFlag) {
            Intent updateIntent = new Intent(WeatherInfoActivity.this, UpdateWeatherInfoService.class);
            stopService(updateIntent);
            updateIntent.putExtra("update_times", updateTime);
            startService(updateIntent);
        }
    }

    private void queryWeather() {
        weatherList = snowWeatherDB.loadWeather(currentCode, currentUrl);
        if (weatherList.size() > 0) {
            for (Weather w : weatherList) {
                currentWeather = w;
            }

            showWeather();
            saveWeatherInfoToPrfs(this);
        } else {
            queryFromServer(currentCode);
        }
    }

    private void queryFromServer(String weatherCode) {
        String address = "";
        if (!TextUtils.isEmpty(weatherCode)) {
            address = "http://flash.weather.com.cn/wmaps/xml/" + weatherCode + ".xml";
        }

        HTTPUtil.sendRequest(address, new HTTPCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if (!TextUtils.isEmpty(response)) {
                    result = HandleHTTPResponse.handleWeatherResponse(response, currentCode, currentUrl, snowWeatherDB);

                    if (result) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                queryWeather();
                            }
                        });
                    }
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        weather_ptime_textview.setText("同步失败，请检查网络设置");
                    }
                });
                e.printStackTrace();
            }
        });
    }

    private void showWeather() {

        weather_name_textview.setText(currentWeather.getCity_name());

        weather_current_date.setText(currentWeather.getWeather_current_date());
        String weather_ptime = currentWeather.getWeather_ptime();
        if (weather_ptime.equals("99:99")) {
            weather_ptime = "12:00";
        }
        weather_ptime_textview.setText(weather_ptime + "发布");

        if (Integer.valueOf(weather_ptime.substring(0, 2)).intValue() < 18) {
            weather_ptime_imageview.setImageResource(R.drawable.day);
        } else {
            weather_ptime_imageview.setImageResource(R.drawable.night);
        }

        weather_desp_textview.setText(currentWeather.getWeather_desp());

        String weather_temp1 = currentWeather.getWeather_temp1();
        String weather_temp2 = currentWeather.getWeather_temp2();
        if (Integer.valueOf(weather_temp1) > Integer.valueOf(weather_temp2)) {
            weather_temp1_textview.setText(weather_temp2 + "℃");
            weather_temp2_textview.setText(weather_temp1 + "℃");
        } else {
            weather_temp1_textview.setText(weather_temp1 + "℃");
            weather_temp2_textview.setText(weather_temp2 + "℃");
        }

        String weather_tempnow = currentWeather.getWeather_temnow();
        if (weather_tempnow.equals("暂无实况")) {
            weather_tempnow = "当前暂无确切温度信息";
            weather_tempnow_textview.setText(weather_tempnow);
        } else {
            weather_tempnow_textview.setText("当前气温" + weather_tempnow + "℃");
        }

        String windDir = currentWeather.getWindDir();
        String extra;
        if (windDir.equals("暂无实况")) {
            extra = "当前暂无风向湿度等信息";
        } else {
            extra = currentWeather.getWindPower() + currentWeather.getWindDir()
                    + "   " + "湿度" + currentWeather.getHumidity();
        }
        weather_extra_textview.setText(extra);

        weather_ptime_imageview.setVisibility(View.VISIBLE);
        weather_name_textview.setVisibility(View.VISIBLE);
        weather_current_date.setVisibility(View.VISIBLE);
        weather_info_layout.setVisibility(View.VISIBLE);
        weather_temp_vary_layout.setVisibility(View.VISIBLE);
    }

    private void saveWeatherInfoToPrfs(Context context) {
        SharedPreferences.Editor editor = PreferenceManager
                .getDefaultSharedPreferences(context).edit();
        editor.putBoolean("city_selected", true);
        editor.putString("weather_code", currentCode);
        editor.putString("weather_url", currentUrl);
        editor.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_rechoose_btn:
                ChooseAreaActivity.startChooseAreaActivity(WeatherInfoActivity.this, true);
                finish();
                break;
            case R.id.weather_update_btn:
                updateWeatherWithTouchingHand();
                break;
            case R.id.weather_setting_btn:
                Intent intent = new Intent(this, WeatherSettingActivity.class);
                startActivityForResult(intent, 1);
                break;

            default:
                break;
        }
    }

    private void updateWeatherWithTouchingHand() {
        weather_update_btn.setClickable(false);
        seekingWeatherInfoShow();
        queryFromServer(currentCode);
        weather_update_btn.setClickable(true);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    boolean serviceFlag = data.getBooleanExtra("service_flag", false);
                    int times = data.getIntExtra("back_result", 1);
                    Intent updateIntent = new Intent(this, UpdateWeatherInfoService.class);
                    if (!serviceFlag) {
                        stopService(updateIntent);
                    } else {
                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
                        int isChosenTimes = sharedPreferences.getInt("update_time", 1);
                        if (isChosenTimes != times) {
                            stopService(updateIntent);
                        }
                        updateIntent.putExtra("update_times", times);
                        startService(updateIntent);
                    }

                    saveUpdateServiceInfoToPrfs(serviceFlag, times);
                }
                break;

            default:
                break;
        }
    }

    private void saveUpdateServiceInfoToPrfs(boolean serviceFlag, int times) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean("service_flag", serviceFlag);
        editor.putInt("update_time", times);
        editor.commit();
    }

    public static void startWeatherActivity(Context context, String weatherCode, String weatherUrl) {
        Intent intent = new Intent(context, WeatherInfoActivity.class);
        intent.putExtra("weather_code", weatherCode);
        intent.putExtra("weather_url", weatherUrl);
        context.startActivity(intent);
    }

}
