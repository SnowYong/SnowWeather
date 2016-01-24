package com.snow.app.snowweather.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.snow.app.snowweather.R;

/**
 * Created by Administrator on 2016.01.24.
 */
public class WeatherTimesListActivity extends Activity {
    private ListView weather_auto_update_dialog_listview;
    private String[] dataList = {"每1小时", "每2小时", "每3小时", "每6小时", "每8小时", "每12小时", "每24小时"};
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.weather_times_list_activity);

        weather_auto_update_dialog_listview = (ListView) findViewById(R.id.weather_auto_update_dialog_listview);
        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, dataList);
        weather_auto_update_dialog_listview.setAdapter(arrayAdapter);

        weather_auto_update_dialog_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String backInfo = dataList[position];
                Intent intent = new Intent();
                intent.putExtra("back_info", backInfo);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
