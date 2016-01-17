package com.snow.app.snowweather.util;

/**
 * Created by Administrator on 2016.01.16.
 */
public interface HTTPCallBackListener {
    void onFinish(String response);

    void onError(Exception e);
}
