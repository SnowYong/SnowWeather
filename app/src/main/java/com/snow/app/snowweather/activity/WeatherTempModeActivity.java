package com.snow.app.snowweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.RadioButton;

import com.snow.app.snowweather.R;

/**
 * Created by Administrator on 2016.01.27.
 */
public class WeatherTempModeActivity extends Activity implements View.OnClickListener {
    private RadioButton weather_temp_mode_ss, weather_temp_mode_hs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_temp_mode_activity);

        weather_temp_mode_ss = (RadioButton) findViewById(R.id.weather_temp_mode_ss);
        weather_temp_mode_hs = (RadioButton) findViewById(R.id.weather_temp_mode_hs);
        weather_temp_mode_ss.setOnClickListener(this);
        weather_temp_mode_hs.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        boolean isChecked = ((RadioButton) v).isChecked();

        switch (v.getId()) {
            case R.id.weather_temp_mode_ss:
                if (isChecked) {
                    sendTempModeBack(weather_temp_mode_ss.getText().toString());
                }
                break;

            case R.id.weather_temp_mode_hs:
                if (isChecked) {
                    sendTempModeBack(weather_temp_mode_hs.getText().toString());
                }
                break;

            default:
                break;
        }
    }

    private void sendTempModeBack(String tempMode) {
        Intent intent = new Intent();
        intent.putExtra("temp_mode", tempMode);
        setResult(RESULT_OK, intent);
        finish();
    }
}
