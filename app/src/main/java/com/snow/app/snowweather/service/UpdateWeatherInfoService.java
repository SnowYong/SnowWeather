package com.snow.app.snowweather.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.snow.app.snowweather.receiver.UpdateWeatherInfoReceiver;
import com.snow.app.snowweather.util.HTTPCallBackListener;
import com.snow.app.snowweather.util.HTTPUtil;
import com.snow.app.snowweather.util.HandleHTTPResponse;

/**
 * Created by Administrator on 2016.01.23.
 */
public class UpdateWeatherInfoService extends Service {
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Intent i;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                updateWeatherInfo();
            }
        }).start();

        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int update_times = intent.getIntExtra("update_times", 8);
        int last_time = update_times * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + last_time;
        i = new Intent(UpdateWeatherInfoService.this, UpdateWeatherInfoReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pendingIntent);
        return super.onStartCommand(intent, flags, startId);
    }

    private void updateWeatherInfo() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        final String county_url = sharedPreferences.getString("county_url", "");
        final String address = "http://flash.weather.com.cn/wmaps/xml/"
                + sharedPreferences.getString("weather_code", "") + ".xml";

        HTTPUtil.sendRequest(address, new HTTPCallBackListener() {
            @Override
            public void onFinish(String response) {
                HandleHTTPResponse.handleWeatherResponse(getApplicationContext(), response, county_url);
            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        alarmManager.cancel(pendingIntent);
    }
}
