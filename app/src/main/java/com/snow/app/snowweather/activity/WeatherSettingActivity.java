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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.snow.app.snowweather.R;
import com.snow.app.snowweather.service.UpdateWeatherInfoService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016.01.24.
 */
public class WeatherSettingActivity extends Activity implements View.OnClickListener, AdapterView.OnItemSelectedListener {
    private Switch weather_update_switch;
    private TextView weather_update_times_textview,
            weather_tempnumbers_mode_textview, weather_dayofweek_mode_textview;
    private LinearLayout weather_update_times_layout,
            weather_tempnumbers_mode_layout, weather_dayofweek_mode_layout;

    private Spinner weather_date_mode_spinner;
    private ArrayAdapter<CharSequence> weather_date_mode_spinner_adapter;


    private static int backResult;
    private static boolean serviceFlag;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_setting_activity);

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        editor = sharedPreferences.edit();
        widgetInit();

    }

    private void widgetInit() {
        weather_update_switch = (Switch) findViewById(R.id.weather_update_switch);

        weather_update_times_textview = (TextView) findViewById(R.id.weather_update_times_textview);
        weather_update_times_layout = (LinearLayout) findViewById(R.id.weather_update_times_layout);

        weather_tempnumbers_mode_textview = (TextView) findViewById(R.id.weather_tempnumbers_mode_textview);
        weather_tempnumbers_mode_layout = (LinearLayout) findViewById(R.id.weather_tempnumbers_mode_layout);

        weather_dayofweek_mode_textview = (TextView) findViewById(R.id.weather_dayofweek_mode_textview);
        weather_dayofweek_mode_layout = (LinearLayout) findViewById(R.id.weather_dayofweek_mode_layout);

        weather_date_mode_spinner = (Spinner) findViewById(R.id.weather_date_mode_spinner);
        weather_date_mode_spinner_adapter = ArrayAdapter.createFromResource(this,
                R.array.weather_date_format, android.R.layout.simple_spinner_item);
        weather_date_mode_spinner_adapter
                .setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        weather_date_mode_spinner.setAdapter(weather_date_mode_spinner_adapter);


        boolean savedServiceFlag = sharedPreferences.getBoolean("service_flag", true);
        String savedText = "每" + sharedPreferences.getInt("update_time", 1) + "小时";
        String tempMode = sharedPreferences.getString("temp_mode", "℃");
        String dayofweekMode = sharedPreferences.getString("dayofweek_mode", "中文星期");
        String dateFormat = sharedPreferences.getString("date_format", "yyyy年MM月dd日");

        if (savedServiceFlag) {
            weather_update_switch.setChecked(true);
            serviceFlag = true;
        } else {
            weather_update_switch.setChecked(false);
            serviceFlag = false;
        }

        weather_update_times_textview.setText(savedText);
        weather_tempnumbers_mode_textview.setText(tempMode);
        weather_dayofweek_mode_textview.setText(dayofweekMode);
        weather_date_mode_spinner.setSelection(weather_date_mode_spinner_adapter.getPosition(dateFormat));

        weather_update_switch.setOnClickListener(this);
        weather_update_times_layout.setOnClickListener(this);
        weather_tempnumbers_mode_layout.setOnClickListener(this);
        weather_dayofweek_mode_layout.setOnClickListener(this);
        weather_date_mode_spinner.setOnItemSelectedListener(this);
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

            case R.id.weather_dayofweek_mode_layout:
                weather_dayofweek_mode_layout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
                Intent dayOfWeekIntent = new Intent(this, WeatherDayofWeekModeActivity.class);
                startActivityForResult(dayOfWeekIntent, 3);
                break;

            default:
                break;
        }
    }

    private void saveUpdateServiceInfoToPrfs() {
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
        weather_dayofweek_mode_layout.setBackgroundColor(getResources().getColor(R.color.colorUnderline));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK) {
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
                    saveUpdateSettingInfoToPrfs(weather_tempnumbers_mode_textview,
                            data, "temp_mode");
//                    String tempMode = data.getStringExtra("temp_mode");
//                    weather_tempnumbers_mode_textview.setText(tempMode);
//                    editor.putString("temp_mode", tempMode);
//                    editor.commit();
                }
                break;

            case 3:
                if (resultCode == RESULT_OK) {
                    saveUpdateSettingInfoToPrfs(weather_dayofweek_mode_textview,
                            data, "dayofweek_mode");
//                    String dayofweekMode = data.getStringExtra("dayofweek_mode");
//                    weather_dayofweek_mode_textview.setText(dayofweekMode);
//                    editor.putString("dayofweek_mode", dayofweekMode);
//                    editor.commit();
                }
                break;

            default:
                break;
        }
    }

    private void saveUpdateSettingInfoToPrfs(TextView view, Intent data, String whichMode) {
        String mode = data.getStringExtra(whichMode);
        view.setText(mode);
        editor.putString(whichMode, mode);
        editor.commit();
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
        setResult(RESULT_OK, intent);
        finish();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String dateFormat = (String) parent.getSelectedItem();
        editor.putString("date_format", dateFormat);
        editor.commit();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}
