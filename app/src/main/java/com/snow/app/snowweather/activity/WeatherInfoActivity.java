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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.snow.app.snowweather.R;
import com.snow.app.snowweather.db.SnowWeatherDB;
import com.snow.app.snowweather.service.UpdateWeatherInfoService;
import com.snow.app.snowweather.util.HTTPCallBackListener;
import com.snow.app.snowweather.util.HTTPUtil;
import com.snow.app.snowweather.util.HandleHTTPResponse;

/**
 * Created by Administrator on 2016.01.18.
 */
public class WeatherInfoActivity extends Activity implements View.OnClickListener {
    private LinearLayout weather_info_layout, weather_temp_vary_layout;
    private TextView weather_name_textview, weather_ptime_textview, weather_current_date,
            weather_desp_textview, weather_temp1_textview, weather_temp2_textview;
    private TextView weather_tempnow_textview, weather_extra_textview;
    private ImageButton weather_rechoose_btn, weather_update_btn;
    private boolean click_flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_info_activity);

        weather_name_textview = (TextView) findViewById(R.id.weather_name_textview);
        weather_ptime_textview = (TextView) findViewById(R.id.weather_ptime_textview);
        weather_info_layout = (LinearLayout) findViewById(R.id.weather_info_layout);
        weather_temp_vary_layout = (LinearLayout) findViewById(R.id.weather_temp_vary_layout);
        weather_current_date = (TextView) findViewById(R.id.weather_current_date);
        weather_desp_textview = (TextView) findViewById(R.id.weather_desp_textview);
        weather_temp1_textview = (TextView) findViewById(R.id.weather_temp1_textview);
        weather_temp2_textview = (TextView) findViewById(R.id.weather_temp2_textview);
        weather_tempnow_textview = (TextView) findViewById(R.id.weather_tempnow_textview);
        weather_extra_textview = (TextView) findViewById(R.id.weather_extra_textview);
        weather_rechoose_btn = (ImageButton) findViewById(R.id.weather_rechoose_btn);
        weather_update_btn = (ImageButton) findViewById(R.id.weather_update_btn);
        weather_rechoose_btn.setOnClickListener(this);
        weather_update_btn.setOnClickListener(this);

        String county_code = getIntent().getStringExtra("county_code");
        String county_url = getIntent().getStringExtra("county_url");
        if (!TextUtils.isEmpty(county_code)) {
            weather_name_textview.setVisibility(View.INVISIBLE);
            weather_info_layout.setVisibility(View.INVISIBLE);
            weather_temp_vary_layout.setVisibility(View.INVISIBLE);
            weather_ptime_textview.setText("正在同步中...");
            click_flag = true;
            queryWeatherInfo(county_code, county_url);
        } else {
            showWeather();
        }

        Intent intent = new Intent(WeatherInfoActivity.this, UpdateWeatherInfoService.class);
        startService(intent);
    }

    private void queryWeatherInfo(final String code, final String url) {
        String address = "http://flash.weather.com.cn/wmaps/xml/" + code + ".xml";

        HTTPUtil.sendRequest(address, new HTTPCallBackListener() {
            @Override
            public void onFinish(String response) {
                if (!TextUtils.isEmpty(response)) {
                    HandleHTTPResponse.handleWeatherResponse(WeatherInfoActivity.this, response, url);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            showWeather();
                        }
                    });
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
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(WeatherInfoActivity.this);
        weather_name_textview.setText(sharedPreferences.getString("city_name", ""));

        String weather_ptime = sharedPreferences.getString("weather_ptime", "");
        if (weather_ptime.equals("99:99")) {
            weather_ptime = "12:00";
        }
        weather_ptime_textview.setText("今日" + weather_ptime + "发布");

        weather_current_date.setText(sharedPreferences.getString("weather_current_date", ""));
        weather_desp_textview.setText(sharedPreferences.getString("weather_desp", ""));
        weather_temp1_textview.setText(sharedPreferences.getString("weather_temp1", "") + "℃");
        weather_temp2_textview.setText(sharedPreferences.getString("weather_temp2", "") + "℃");

        String weather_tempnow = sharedPreferences.getString("weather_tempnow", "");
        if (weather_tempnow.equals("暂无实况")) {
            weather_tempnow = sharedPreferences.getString("weather_temp2", "");
        }
        weather_tempnow_textview.setText("当前气温" + weather_tempnow + "℃");

        String windDir = sharedPreferences.getString("windDir", "");
        String extra;
        if (windDir.equals("暂无实况")) {
            extra = "当前暂无风向湿度等信息";
        } else {
            extra = sharedPreferences.getString("windPower", "") + sharedPreferences.getString("windDir", "")
                    + "   " + "湿度" + sharedPreferences.getString("humidity", "");
        }
        weather_extra_textview.setText(extra);

        weather_name_textview.setVisibility(View.VISIBLE);
        weather_info_layout.setVisibility(View.VISIBLE);
        weather_temp_vary_layout.setVisibility(View.VISIBLE);
        click_flag = false;
    }

    public static void startWeatherActivity(Context context, String county_code, String county_url) {
        Intent intent = new Intent(context, WeatherInfoActivity.class);
        intent.putExtra("county_code", county_code);
        intent.putExtra("county_url", county_url);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_rechoose_btn:
                ChooseAreaActivity.startChooseAreaActivity(WeatherInfoActivity.this, true);
                finish();
                break;
            case R.id.weather_update_btn:
                if (!click_flag) {
                    updateWeatherWithTouchingHand();
                }
                break;
            default:
                break;
        }
    }

    private void updateWeatherWithTouchingHand() {
        click_flag = true;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String county_code = sharedPreferences.getString("weather_code", "");
        String county_url = sharedPreferences.getString("county_url", "");
        weather_name_textview.setVisibility(View.INVISIBLE);
        weather_info_layout.setVisibility(View.INVISIBLE);
        weather_temp_vary_layout.setVisibility(View.INVISIBLE);
        weather_ptime_textview.setText("正在同步中...");
        queryWeatherInfo(county_code, county_url);
    }
}
