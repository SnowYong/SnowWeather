package com.snow.app.snowweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

import com.snow.app.snowweather.R;

/**
 * Created by Administrator on 2016.01.28.
 */
public class WeatherDayofWeekModeActivity extends Activity implements View.OnClickListener {
    private RadioButton weather_dayofweek_mode_cn, weather_dayofweek_mode_en;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_dayofweek_mode_activity);

        weather_dayofweek_mode_cn = (RadioButton) findViewById(R.id.weather_dayofweek_mode_cn);
        weather_dayofweek_mode_en = (RadioButton) findViewById(R.id.weather_dayofweek_mode_en);
        weather_dayofweek_mode_cn.setOnClickListener(this);
        weather_dayofweek_mode_en.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean isChecked = ((RadioButton) v).isChecked();

        switch (v.getId()) {
            case R.id.weather_dayofweek_mode_cn:
                if (isChecked) {
                    sendDayofWeekModeBack(weather_dayofweek_mode_cn.getText().toString());
                }
                break;

            case R.id.weather_dayofweek_mode_en:
                if (isChecked) {
                    sendDayofWeekModeBack(weather_dayofweek_mode_en.getText().toString());
                }
                break;

            default:
                break;
        }
    }

    private void sendDayofWeekModeBack(String dayofweekMode) {
        Intent intent = new Intent();
        intent.putExtra("dayofweek_mode", dayofweekMode);
        setResult(RESULT_OK, intent);
        finish();
    }
}
