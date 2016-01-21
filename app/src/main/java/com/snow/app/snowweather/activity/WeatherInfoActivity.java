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
import com.snow.app.snowweather.util.HTTPCallBackListener;
import com.snow.app.snowweather.util.HTTPUtil;
import com.snow.app.snowweather.util.HandleHTTPResponse;

/**
 * Created by Administrator on 2016.01.18.
 */
public class WeatherInfoActivity extends Activity implements View.OnClickListener {
    private LinearLayout weather_info_layout;
    private TextView weather_name_textview, weather_ptime_textview, weather_current_date,
            weather_desp_textview, weather_temp1_textview, weather_temp2_textview;
    private ImageButton weather_rechoose_btn, weather_update_btn;

    private SnowWeatherDB snowWeatherDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_info_activity);

        snowWeatherDB = SnowWeatherDB.getInstance(this);
        weather_name_textview = (TextView) findViewById(R.id.weather_name_textview);
        weather_ptime_textview = (TextView) findViewById(R.id.weather_ptime_textview);
        weather_info_layout = (LinearLayout) findViewById(R.id.weather_info_layout);
        weather_current_date = (TextView) findViewById(R.id.weather_current_date);
        weather_desp_textview = (TextView) findViewById(R.id.weather_desp_textview);
        weather_temp1_textview = (TextView) findViewById(R.id.weather_temp1_textview);
        weather_temp2_textview = (TextView) findViewById(R.id.weather_temp2_textview);
        weather_rechoose_btn = (ImageButton) findViewById(R.id.weather_rechoose_btn);
        weather_update_btn = (ImageButton) findViewById(R.id.weather_update_btn);
        weather_rechoose_btn.setOnClickListener(this);
        weather_update_btn.setOnClickListener(this);

        String county_code = getIntent().getStringExtra("county_code");
        if (!TextUtils.isEmpty(county_code)) {
            weather_name_textview.setVisibility(View.INVISIBLE);
            weather_info_layout.setVisibility(View.INVISIBLE);
            weather_ptime_textview.setText("正在同步中...");
            queryWeatherInfo(county_code);
        } else {
            showWeather();
        }
    }

    private void queryWeatherInfo(final String code) {
        String address = "http://www.weather.com.cn/adat/cityinfo/" + code + ".html";

        HTTPUtil.sendRequest(address, new HTTPCallBackListener() {
            @Override
            public void onFinish(String response) {
                if (!TextUtils.isEmpty(response)) {
                    HandleHTTPResponse.handleWeatherResponse(WeatherInfoActivity.this, response);

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
        weather_ptime_textview.setText(sharedPreferences.getString("weather_ptime", "") + "发布");
        weather_current_date.setText(sharedPreferences.getString("weather_current_data", ""));
        weather_desp_textview.setText(sharedPreferences.getString("weather_desp", ""));
        weather_temp1_textview.setText(sharedPreferences.getString("weather_temp1", ""));
        weather_temp2_textview.setText(sharedPreferences.getString("weather_temp2", ""));
        weather_name_textview.setVisibility(View.VISIBLE);
        weather_info_layout.setVisibility(View.VISIBLE);
    }

    public static void startWeatherActivity(Context context, String county_code) {
        Intent intent = new Intent(context, WeatherInfoActivity.class);
        intent.putExtra("county_code", county_code);
        context.startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_rechoose_btn:
                ChooseAreaActivity.startChooseAreaActivity(WeatherInfoActivity.this, true);
                snowWeatherDB.cleanCity();
                snowWeatherDB.cleanCounty();
                finish();
                break;
            case R.id.weather_update_btn:
                updateWeatherWithTouchingHand();
                break;
            default:
                break;
        }
    }

    private void updateWeatherWithTouchingHand() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String county_code = sharedPreferences.getString("weather_code", "");
        weather_name_textview.setVisibility(View.INVISIBLE);
        weather_info_layout.setVisibility(View.INVISIBLE);
        weather_ptime_textview.setText("正在同步中...");
        queryWeatherInfo(county_code);
    }
}
