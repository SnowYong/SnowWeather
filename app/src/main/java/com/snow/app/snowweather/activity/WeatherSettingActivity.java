package com.snow.app.snowweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.snow.app.snowweather.R;
import com.snow.app.snowweather.service.UpdateWeatherInfoService;

/**
 * Created by Administrator on 2016.01.24.
 */
public class WeatherSettingActivity extends Activity implements View.OnClickListener {
    private Switch weather_update_switch;
    private TextView weather_update_times_textview, weather_tempnumbers_mode_textview;
    private LinearLayout weather_update_times_layout, weather_tempnumbers_mode_layout;

    private static int backResult;
    private static boolean serviceFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_setting_activity);

        widgetInit();

    }

    private void widgetInit() {
        weather_update_switch = (Switch) findViewById(R.id.weather_update_switch);
        weather_update_times_textview = (TextView) findViewById(R.id.weather_update_times_textview);
        weather_update_times_layout = (LinearLayout) findViewById(R.id.weather_update_times_layout);
        weather_tempnumbers_mode_textview = (TextView) findViewById(R.id.weather_tempnumbers_mode_textview);
        weather_tempnumbers_mode_layout = (LinearLayout) findViewById(R.id.weather_tempnumbers_mode_layout);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean savedServiceFlag = sharedPreferences.getBoolean("service_flag", true);
        String savedText = "每" + sharedPreferences.getInt("update_time", 1) + "小时";
        String tempMode = sharedPreferences.getString("temp_mode", "℃");

        if (savedServiceFlag) {
            weather_update_switch.setChecked(true);
            serviceFlag = true;
        } else {
            weather_update_switch.setChecked(false);
            serviceFlag = false;
        }

        if (!TextUtils.isEmpty(savedText)) {
            weather_update_times_textview.setText(savedText);
        }

        weather_tempnumbers_mode_textview.setText(tempMode);

        weather_update_switch.setOnClickListener(this);
        weather_update_times_layout.setOnClickListener(this);
        weather_tempnumbers_mode_layout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.weather_update_switch:
                Intent updateIntent = new Intent(this, UpdateWeatherInfoService.class);
                if (weather_update_switch.isChecked()) {
                    serviceFlag = true;
                    int updateTimes = getTrueTime(weather_update_times_textview.getText().toString().trim());
                    updateIntent.putExtra("update_times", updateTimes);
                    startService(updateIntent);
                    saveUpdateServiceInfoToPrfs();
                } else {
                    serviceFlag = false;
                    stopService(updateIntent);
                    saveUpdateServiceInfoToPrfs();
                }
                break;

            case R.id.weather_update_times_layout:
                weather_update_times_layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                Intent dialogIntent = new Intent(this, WeatherTimesListActivity.class);
                startActivityForResult(dialogIntent, 1);
                break;

            case R.id.weather_tempnumbers_mode_layout:
                weather_tempnumbers_mode_layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                Intent tempModeIntent = new Intent(this, WeatherTempModeActivity.class);
                startActivityForResult(tempModeIntent, 2);
                break;

            default:
                break;
        }
    }

    private void saveUpdateServiceInfoToPrfs() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        boolean flag = serviceFlag;
        int result = getTrueTime(weather_update_times_textview.getText().toString().trim());
        editor.putBoolean("service_flag", flag);
        editor.putInt("update_time", result);
        editor.commit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        weather_update_times_layout.setBackgroundColor(getResources().getColor(R.color.colorUnderline));
        weather_tempnumbers_mode_layout.setBackgroundColor(getResources().getColor(R.color.colorUnderline));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
                    SharedPreferences sharedPreferences = PreferenceManager
                            .getDefaultSharedPreferences(this);
                    boolean savedServiceFlag = sharedPreferences.getBoolean("service_flag", true);
                    String savedText = "每" + sharedPreferences.getInt("update_time", 1) + "小时";

                    String backInfo = data.getStringExtra("back_info");
                    if (savedServiceFlag) {
                        if (!savedText.equals(backInfo)) {
                            Intent updateIntent = new Intent(this, UpdateWeatherInfoService.class);
                            stopService(updateIntent);
                            int updateTimes = getTrueTime(backInfo);
                            updateIntent.putExtra("update_times", updateTimes);
                            startService(updateIntent);
                            weather_update_times_textview.setText(backInfo);
                            saveUpdateServiceInfoToPrfs();
                        }
                    } else {
                        weather_update_times_textview.setText(backInfo);
                        saveUpdateServiceInfoToPrfs();
                    }
                }
                break;

            case 2:
                if (resultCode == RESULT_OK) {
                    String tempMode = data.getStringExtra("temp_mode");
                    weather_tempnumbers_mode_textview.setText(tempMode);
                    SharedPreferences.Editor editor = PreferenceManager
                            .getDefaultSharedPreferences(this).edit();
                    editor.putString("temp_mode", tempMode);
                    editor.commit();
                }
                break;

            default:
                break;
        }
    }


    private int getTrueTime(String originTime) {
        if (!TextUtils.isEmpty(originTime)) {
            String result;
            String temp = originTime.substring(1, 3);
            if (temp.contains("小")) {
                result = temp.replace("小", "");
                backResult = Integer.valueOf(result).intValue();
            } else {
                backResult = Integer.valueOf(temp).intValue();
            }
        }
        return backResult;
    }

    @Override
    public void onBackPressed() {
        saveUpdateServiceInfoToPrfs();
        Intent intent = new Intent();
        String tempMode = weather_tempnumbers_mode_textview.getText().toString();
        intent.putExtra("temp_mode", tempMode);
        setResult(RESULT_OK, intent);
        finish();
    }
}
