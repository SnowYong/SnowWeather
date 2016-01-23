package com.snow.app.snowweather.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.snow.app.snowweather.service.UpdateWeatherInfoService;

/**
 * Created by Administrator on 2016.01.23.
 */
public class UpdateWeatherInfoReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, UpdateWeatherInfoService.class);
        context.startService(i);
    }
}
