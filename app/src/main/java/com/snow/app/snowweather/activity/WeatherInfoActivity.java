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

import com.snow.app.snowweather.R;
import com.snow.app.snowweather.db.SnowWeatherDB;
import com.snow.app.snowweather.model.Weather;
import com.snow.app.snowweather.service.UpdateWeatherInfoService;
import com.snow.app.snowweather.util.WeatherImageSrc;
import com.snow.app.snowweather.util.HTTPCallBackListener;
import com.snow.app.snowweather.util.HTTPUtil;
import com.snow.app.snowweather.util.HandleHTTPResponse;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Administrator on 2016.01.18.
 */
public class WeatherInfoActivity extends Activity implements View.OnClickListener {
    private LinearLayout weather_info_layout, weather_temp_vary_img_layout, weather_temp_vary_layout;
    private TextView weather_name_textview, weather_ptime_textview, weather_current_date,
            weather_desp_textview, weather_temp1_textview, weather_temp2_textview, weather_temp_vary_imgtext;
    private TextView weather_tempnow_textview, weather_extra_textview, weather_dayofweek_textview;
    private ImageButton weather_rechoose_btn, weather_update_btn, weather_setting_btn;
    private ImageView weather_ptime_imageview, weather_state_imageview1, weather_state_imageview2;

    private List<Weather> weatherList;
    private SnowWeatherDB snowWeatherDB;

    private Weather currentWeather;
    private String currentCode;
    private String currentUrl;

    private String[] chineseWeek = {"星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
    private String[] foreignWeek = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thirsday", "Friday", "Saturday"};

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
            seekingWeatherInfoShow();
            queryFromServer(currentCode);
        }

        serviceInit();
    }

    private void seekingWeatherInfoShow() {
        weather_state_imageview1.setVisibility(View.INVISIBLE);
        weather_state_imageview2.setVisibility(View.INVISIBLE);
        weather_name_textview.setVisibility(View.INVISIBLE);
        weather_current_date.setVisibility(View.INVISIBLE);
        weather_dayofweek_textview.setVisibility(View.INVISIBLE);
        weather_info_layout.setVisibility(View.INVISIBLE);
        weather_temp_vary_layout.setVisibility(View.INVISIBLE);
        weather_temp_vary_img_layout.setVisibility(View.INVISIBLE);
        weather_ptime_textview.setText("正在同步中...");
    }

    private void widgetInit() {
        weather_name_textview = (TextView) findViewById(R.id.weather_name_textview);
        weather_ptime_textview = (TextView) findViewById(R.id.weather_ptime_textview);
        weather_dayofweek_textview = (TextView) findViewById(R.id.weather_dayofweek_textview);

        weather_info_layout = (LinearLayout) findViewById(R.id.weather_info_layout);
        weather_temp_vary_layout = (LinearLayout) findViewById(R.id.weather_temp_vary_layout);
        weather_temp_vary_img_layout = (LinearLayout) findViewById(R.id.weather_temp_vary_img_layout);

        weather_current_date = (TextView) findViewById(R.id.weather_current_date);
        weather_ptime_imageview = (ImageView) findViewById(R.id.weather_ptime_imageview);
        weather_temp_vary_imgtext = (TextView) findViewById(R.id.weather_temp_vary_imgtext);

        weather_desp_textview = (TextView) findViewById(R.id.weather_desp_textview);
        weather_temp1_textview = (TextView) findViewById(R.id.weather_temp1_textview);
        weather_temp2_textview = (TextView) findViewById(R.id.weather_temp2_textview);
        weather_tempnow_textview = (TextView) findViewById(R.id.weather_tempnow_textview);
        weather_extra_textview = (TextView) findViewById(R.id.weather_extra_textview);

        weather_state_imageview1 = (ImageView) findViewById(R.id.weather_state_imageview1);
        weather_state_imageview2 = (ImageView) findViewById(R.id.weather_state_imageview2);

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
            currentWeather = weatherList.get(weatherList.size() - 1);

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

        showNormalView();

        showDateInfoView();

        showWeekInfoView();

        showWeatherDespView();

        showTempView();

        showWidgetVisibility();

    }


    private void showNormalView() {
        weather_name_textview.setText(currentWeather.getCity_name());

        String weather_ptime = currentWeather.getWeather_ptime();
        if (weather_ptime.equals("99:99")) {
            weather_ptime = "12:00";
        }
        weather_ptime_textview.setText(weather_ptime + "发布");
        if (Integer.valueOf(weather_ptime.substring(0, 2)).intValue() < 18) {
            weather_ptime_imageview.setImageResource(R.drawable.day);
            weather_temp_vary_imgtext.setText("白天气温");
        } else {
            weather_ptime_imageview.setImageResource(R.drawable.night);
            weather_temp_vary_imgtext.setText("夜间气温");
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
    }

    private void showDateInfoView() {
        weather_current_date.setText(currentWeather.getWeather_current_date());
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String dateResult = sharedPreferences.getString("date_format", "yyyy年MM月dd日");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateResult);
        String dateSet = simpleDateFormat.format(new Date());
        weather_current_date.setText(dateSet);
    }

    private void showWeekInfoView() {
        SharedPreferences sharedPreferences = PreferenceManager
                .getDefaultSharedPreferences(this);
        String dayofweekMode = sharedPreferences.getString("dayofweek_mode", "中文星期");

        int dayOfWeek = currentWeather.getWeather_current_dayofweek();
        if (dayofweekMode.equals("Foreign Week")) {
            weather_dayofweek_textview.setText(foreignWeek[dayOfWeek - 1]);
            String pTimeText = weather_ptime_textview.getText().toString().replace("发布", "push");
            weather_ptime_textview.setText(pTimeText);
        } else {
            weather_dayofweek_textview.setText(chineseWeek[dayOfWeek - 1]);
        }
    }

    private void showWeatherDespView() {
        WeatherImageSrc weatherImageSrc = WeatherImageSrc.getInstance();
        String weatherDesp = currentWeather.getWeather_desp();
        weather_desp_textview.setText(weatherDesp);
        if (weatherDesp.contains("转")) {
            String[] splitWeather = weatherDesp.split("转");
            weather_state_imageview1
                    .setImageResource(weatherImageSrc.getWeatherImgSrc(splitWeather[0]));
            weather_state_imageview2
                    .setImageResource(weatherImageSrc.getWeatherImgSrc(splitWeather[1]));
            weather_state_imageview1.setVisibility(View.VISIBLE);
            weather_state_imageview2.setVisibility(View.VISIBLE);
        } else {
            weather_state_imageview1
                    .setImageResource(weatherImageSrc.getWeatherImgSrc(weatherDesp));
            weather_state_imageview1.setVisibility(View.VISIBLE);
            weather_state_imageview2.setVisibility(View.GONE);
        }
    }


    private void setCorrectTempInfo(String tempNow, String temp1, String temp2) {
        if (Integer.valueOf(tempNow) < Integer.valueOf(temp1)) {
            weather_temp1_textview.setText(tempNow + "℃");
        }
        if (Integer.valueOf(tempNow) > Integer.valueOf(temp2)) {
            weather_temp2_textview.setText(tempNow + "℃");
        }
    }

    private void setCorretTempMode(String tempText1, String tempText2) {
        int temp1HSMode = (int) (Integer.valueOf(tempText1.replace("℃", "")).intValue() * 1.8 + 32);
        int temp2HSMode = (int) (Integer.valueOf(tempText2.replace("℃", "")).intValue() * 1.8 + 32);
        weather_temp1_textview.setText(temp1HSMode + "℉");
        weather_temp2_textview.setText(temp2HSMode + "℉");
    }

    private void showTempView() {
        String weather_tempnow = currentWeather.getWeather_temnow();
        String weather_temp1 = currentWeather.getWeather_temp1();
        String weather_temp2 = currentWeather.getWeather_temp2();
        if (Integer.valueOf(weather_temp1) > Integer.valueOf(weather_temp2)) {
            weather_temp1_textview.setText(weather_temp2 + "℃");
            weather_temp2_textview.setText(weather_temp1 + "℃");
            if (!weather_tempnow.equals("暂无实况")) {
                setCorrectTempInfo(weather_tempnow, weather_temp2, weather_temp1);
            }
        } else {
            weather_temp1_textview.setText(weather_temp1 + "℃");
            weather_temp2_textview.setText(weather_temp2 + "℃");
            if (!weather_tempnow.equals("暂无实况")) {
                setCorrectTempInfo(weather_tempnow, weather_temp1, weather_temp2);
            }
        }
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tempMode = sharedPreferences.getString("temp_mode", "℃");
        if (tempMode.equals("℉")) {
            setCorretTempMode(weather_temp1_textview.getText().toString(),
                    weather_temp2_textview.getText().toString());
        }

        if (weather_tempnow.equals("暂无实况")) {
            weather_tempnow = "当前暂无确切温度信息";
            weather_tempnow_textview.setText(weather_tempnow);
        } else {
            weather_tempnow_textview.setText("当前气温" + weather_tempnow + "℃");
            if (tempMode.equals("℉")) {
                int tempNowHSMode = (int) (Integer.valueOf(weather_tempnow).intValue() * 1.8 + 32);
                weather_tempnow_textview.setText("当前气温" + tempNowHSMode + "℉");
            }
            weather_tempnow_textview.setTextColor(getResources().getColor(R.color.colorUnderline));
        }
    }


    private void showWidgetVisibility() {
        weather_dayofweek_textview.setVisibility(View.VISIBLE);
        weather_name_textview.setVisibility(View.VISIBLE);
        weather_current_date.setVisibility(View.VISIBLE);
        weather_info_layout.setVisibility(View.VISIBLE);
        weather_temp_vary_layout.setVisibility(View.VISIBLE);
        weather_temp_vary_img_layout.setVisibility(View.VISIBLE);
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


    public static void startWeatherActivity(Context context, String weatherCode, String weatherUrl) {
        Intent intent = new Intent(context, WeatherInfoActivity.class);
        intent.putExtra("weather_code", weatherCode);
        intent.putExtra("weather_url", weatherUrl);
        context.startActivity(intent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                showWeather();
                break;

            default:
                break;
        }
    }
}
