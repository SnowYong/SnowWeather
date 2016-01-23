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

import com.snow.app.snowweather.receiver.UpdateWeatherInfoReceiver;
import com.snow.app.snowweather.util.HTTPCallBackListener;
import com.snow.app.snowweather.util.HTTPUtil;
import com.snow.app.snowweather.util.HandleHTTPResponse;

/**
 * Created by Administrator on 2016.01.23.
 */
public class UpdateWeatherInfoService extends Service {
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

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        int last_time = 8 * 60 * 60 * 1000;
        long triggerAtTime = SystemClock.elapsedRealtime() + last_time;
        Intent i = new Intent(UpdateWeatherInfoService.this, UpdateWeatherInfoReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, i, 0);
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
}
