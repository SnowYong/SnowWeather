package com.snow.app.snowweather.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.snow.app.snowweather.R;
import com.snow.app.snowweather.db.SnowWeatherDB;
import com.snow.app.snowweather.model.City;
import com.snow.app.snowweather.model.County;
import com.snow.app.snowweather.model.Province;
import com.snow.app.snowweather.util.HTTPCallBackListener;
import com.snow.app.snowweather.util.HTTPUtil;
import com.snow.app.snowweather.util.HandleHTTPResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016.01.16.
 */
public class ChooseAreaActivity extends Activity {
    private TextView area_name_textview;
    private ListView choose_area_listview;
    private List<String> choose_area_list = new ArrayList<String>();
    private ArrayAdapter<String> choose_area_adapter;

    public static final int PROVINCE_LEVEL = 0;
    public static final int CITY_LEVEL = 1;
    public static final int COUNTY_LEVEL = 2;
    private int current_level;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private SnowWeatherDB snowWeatherDB;

    private Province select_province;
    private City select_city;

    private ProgressDialog progressDialog;

    private boolean extra_city_click;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean is_from_weather = getIntent().getBooleanExtra("is_from_weather", false);
        if (sharedPreferences.getBoolean("city_selected", false) && !is_from_weather) {
            Intent intent = new Intent(ChooseAreaActivity.this, WeatherInfoActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area_activity);

        extra_city_click = false;
        snowWeatherDB = SnowWeatherDB.getInstance(this);
        area_name_textview = (TextView) findViewById(R.id.area_name_textview);
        choose_area_listview = (ListView) findViewById(R.id.choose_area_listview);
        choose_area_adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, choose_area_list);
        choose_area_listview.setAdapter(choose_area_adapter);
        choose_area_listview.setFocusable(true);
        choose_area_listview.setFocusableInTouchMode(true);
        choose_area_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (current_level == PROVINCE_LEVEL) {
                    select_province = provinceList.get(position);
                    String province_name = select_province.getProvince_name();
                    if (province_name.equals("北京") || province_name.equals("天津") || province_name.equals("上海") || province_name.equals("重庆")
                            || province_name.equals("香港") || province_name.equals("澳门") || province_name.equals("台湾")) {
                        extra_city_click = true;
                    }
                    queryCity();
                } else if (current_level == CITY_LEVEL) {
                    select_city = cityList.get(position);
                    if (extra_city_click) {
                        String weather_code = select_province.getProvince_code();
                        String weather_url = select_city.getCity_code();
                        WeatherInfoActivity.startWeatherActivity(ChooseAreaActivity.this, weather_code, weather_url);
                        finish();
                    } else {
                        queryCounty();
                    }
                } else if (current_level == COUNTY_LEVEL) {
                    String weather_code = select_city.getCity_code();
                    String weather_url = countyList.get(position).getCounty_code();
                    WeatherInfoActivity.startWeatherActivity(ChooseAreaActivity.this, weather_code, weather_url);
                    finish();
                }
            }
        });

        queryProvince();
    }

    private void queryProvince() {
        provinceList = snowWeatherDB.loadProvince();
        if (provinceList.size() > 0) {
            choose_area_list.clear();
            for (Province p : provinceList) {
                choose_area_list.add(p.getProvince_name());
            }
            area_name_textview.setText("中国");
            choose_area_adapter.notifyDataSetChanged();
            choose_area_listview.setSelection(0);
            current_level = PROVINCE_LEVEL;
        } else {
            queryFromServer(null, "Province");
        }
    }

    private void queryCity() {
        cityList = snowWeatherDB.loadCity(select_province.getId());

        if (cityList.size() > 0) {
            choose_area_list.clear();
            for (City c : cityList) {
                choose_area_list.add(c.getCity_name());
            }
            area_name_textview.setText(select_province.getProvince_name());
            choose_area_adapter.notifyDataSetChanged();
            choose_area_listview.setSelection(0);
            current_level = CITY_LEVEL;
        } else {
            queryFromServer(select_province.getProvince_code(), "City");
        }
    }

    private void queryCounty() {
        countyList = snowWeatherDB.loadCounty(select_city.getId());

        if (countyList.size() > 0) {
            choose_area_list.clear();
            for (County co : countyList) {
                choose_area_list.add(co.getCounty_name());
            }
            area_name_textview.setText(select_city.getCity_name());
            choose_area_adapter.notifyDataSetChanged();
            choose_area_listview.setSelection(0);
            current_level = COUNTY_LEVEL;
        } else {
            queryFromServer(select_city.getCity_code(), "County");
        }
    }

    private void queryFromServer(final String code, final String type) {
        String address;
        if (TextUtils.isEmpty(code)) {
            address = "http://flash.weather.com.cn/wmaps/xml/china.xml";
        } else {
            address = "http://flash.weather.com.cn/wmaps/xml/" + code + ".xml";
        }

        showProgressDialog();
        HTTPUtil.sendRequest(address, new HTTPCallBackListener() {
            @Override
            public void onFinish(String response) {
                boolean result = false;
                if (type.equals("Province")) {
                    result = HandleHTTPResponse.handleProvinceResponse(response, snowWeatherDB);
                } else if (type.equals("City")) {
                    result = HandleHTTPResponse.handleCityResponse(response, select_province.getId(), snowWeatherDB, extra_city_click);
                } else if (type.equals("County")) {
                    result = HandleHTTPResponse.handleCountyResponse(response, select_city.getId(), snowWeatherDB);
                }

                if (result) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if (type.equals("Province")) {
                                queryProvince();
                            } else if (type.equals("City")) {
                                queryCity();
                            } else if (type.equals("County")) {
                                queryCounty();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this, "加载失败,请检查网络设置", Toast.LENGTH_SHORT).show();
                        queryProvince();
                    }
                });
            }
        });
    }

    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("拼命加载中...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
        if (current_level == COUNTY_LEVEL) {
            queryCity();
        } else if (current_level == CITY_LEVEL) {
            queryProvince();
        } else {
            finish();
        }
    }

    public static void startChooseAreaActivity(Context context, boolean is_from_weather) {
        Intent intent = new Intent(context, ChooseAreaActivity.class);
        intent.putExtra("is_from_weather", is_from_weather);
        context.startActivity(intent);
    }
}
