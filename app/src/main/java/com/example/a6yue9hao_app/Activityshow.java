package com.example.a6yue9hao_app;

import android.os.Handler;
import android.util.Log;

import com.example.a6yue9hao_app.databinding.ActivityShowBinding;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import simple.app.SimpleActivity;
import simple.emqx.MqttMessage;
import simple.emqx.MqttTools;
import simple.emqx.lang.IMqttCallBack;

public class Activityshow extends SimpleActivity implements IMqttCallBack {
    private ActivityShowBinding binding;
    MqttTools mqttTools;

    private boolean isDeviceOn =false,isDeviceOn1=false,isDeviceOn2=false,isDeviceOn3=false;//设备状态
    private static final String TAG = "MainActivity";

    private static final String TIME_API_URL = "http://worldtimeapi.org/api/timezone/Asia/Shanghai";
    private Handler timeUpdateHandler = new Handler();
    private Runnable timeUpdateRunnable;

    String wendu,shidu,light,huoyan,yanwu,jiaquan;
    String CO2,CH2O,TVOC,PM25,PM10,Mwendu,Mshidu;
    @Override
    protected void onCreate() {
        binding = ActivityShowBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mqttTools =new MqttTools();
        mqttTools.setHostUrl("tcp://10.1.1.89");
        mqttTools.setUserName("");
        mqttTools.setPassword("");
        mqttTools.setClientId("");
        mqttTools.setCallback(this);
        requestPermissions();//申请权限

        mqttTools.connect(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken iMqttToken) {
                mqttTools.subscribe("sl_pst",0);
                mqttTools.subscribe("sl_sub",0);
                iapp.tw("连接成功");//短暂的时间提示
                Log.d("MQTT","连接成功");//调试debug
                Log.e("连接成功","mqtt");//输出错误error

            }

            @Override
            public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
                iapp.tw("连接失败");
                Log.d("MQTT","连接失败");
            }
        });
         binding.back.setOnClickListener(v->{
             startActivity(TeamActivity.class);
             finish();
         });

        // 开始定期获取网络时间
        startNetworkTimeUpdates();

    }

    @Override
    public void onDisconnected(Throwable throwable) {
        finish();
    }

    @Override
    public void onMessage(String s, MqttMessage mqttMessage) throws Exception {
        if(s.equals("sl_pst") && mqttMessage.isJSONObject()){
            JSONObject data=mqttMessage.getJSONObject();
            runOnUiThread(()->{
                try{
                    if(data.has("temperature")){
                        binding.temperatureText.setText(data.getString("temperature")+"℃");
                    }
                    if(data.has("humidity")){
                        binding.humidityText.setText(data.getString("humidity")+"%");
                    }
                    if(data.has("light")){
                        binding.lightText.setText(data.getString("light")+"%");
                    }

                    if(data.has("fire"))
                    {
                        binding.fireText.setText(data.getString("fire")+"%");
                    }
                    if(data.has("smoke"))
                    {
                        binding.smokeText.setText(data.getString("smoke")+"%");
                    }




                }catch(JSONException e){
                    Log.e("UI","数据解析失败",e);
                }
            });
        }

    }

    private void startNetworkTimeUpdates() {
        timeUpdateRunnable = new Runnable() {
            @Override
            public void run() {
                new Thread(() -> {
                    try {
                        URL url = new URL(TIME_API_URL);
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.setRequestMethod("GET");
                        connection.setConnectTimeout(3000);
                        connection.setReadTimeout(3000);

                        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                            BufferedReader reader = new BufferedReader(
                                    new InputStreamReader(connection.getInputStream()));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }
                            reader.close();

                            JSONObject json = new JSONObject(response.toString());
                            String datetime = json.getString("datetime");

                            // 使用Asia/Shanghai时区解析API返回的时间
                            SimpleDateFormat apiFormat = new SimpleDateFormat(
                                    "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
                            apiFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                            Date date = apiFormat.parse(datetime);

                            // 使用相同的Asia/Shanghai时区显示时间
                            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                            timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
                            SimpleDateFormat dateFormat = new SimpleDateFormat(
                                    "yyyy年MM月dd日 EEE", Locale.CHINA);
                            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));

                            final String timeStr = timeFormat.format(date);
                            final String dateStr = dateFormat.format(date);

                            runOnUiThread(() -> {
                                binding.timeText.setText(timeStr);
                                binding.timeText.append(" (本地时间)");
                                binding.dateText.setText(dateStr);
                            });
                        }
                        connection.disconnect();
                    } catch (Exception e) {
                        Log.e(TAG, "获取时间失败", e);
                        // 失败时使用系统时间作为后备
                        updateWithSystemTime();
                    }

                    // 每分钟更新一次
                    timeUpdateHandler.postDelayed(this, 60000);
                }).start();
            }
        };
        timeUpdateHandler.post(timeUpdateRunnable);
    }

    private void updateWithSystemTime() {
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEE", Locale.CHINA);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
        Date now = new Date();

        runOnUiThread(() -> {
            binding.timeText.setText(timeFormat.format(now));
            binding.dateText.setText(dateFormat.format(now));
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 移除定时更新
        if (timeUpdateHandler != null && timeUpdateRunnable != null) {
            timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
        }
    }


    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
