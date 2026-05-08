//package com.example.a6yue9hao_app;
//
//import android.os.Bundle;
//import android.os.Handler;
//import android.util.Log;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.core.graphics.Insets;
//import androidx.core.view.ViewCompat;
//import androidx.core.view.WindowInsetsCompat;
//
//import com.example.a6yue9hao_app.databinding.ActivityMainBinding;
//
//import org.eclipse.paho.client.mqttv3.IMqttActionListener;
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.IMqttToken;
//
//import simple.app.SimpleActivity;
//import simple.emqx.MqttMessage;
//import simple.emqx.MqttTools;
//import simple.emqx.lang.IMqttCallBack;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Locale;
//import java.util.TimeZone;
//
//public class MainActivity extends SimpleActivity implements IMqttCallBack {
//
//        ActivityMainBinding binding;
//        MqttTools mqttTools;
//
//        String wendu,shidu,light,huoyan,yanwu,jiaquan;
//        String CO2,CH2O,TVOC,PM25,PM10,Mwendu,Mshidu;
//
//
//        private boolean isDeviceOn =false,isDeviceOn1=false,isDeviceOn2=false,isDeviceOn3=false;//设备状态
//        private static final String TAG = "MainActivity";
//
//        private static final String TIME_API_URL = "http://worldtimeapi.org/api/timezone/Asia/Shanghai";
//        private Handler timeUpdateHandler = new Handler();
//        private Runnable timeUpdateRunnable;
//
//
//        @Override
//        protected void onCreate() {
//            binding =ActivityMainBinding.inflate(getLayoutInflater());
//            setContentView(binding.getRoot());
//            mqttTools =new MqttTools();
//            mqttTools.setHostUrl("tcp://192.168.53.140");
//            mqttTools.setUserName("");
//            mqttTools.setPassword("");
//            mqttTools.setClientId("");
//            mqttTools.setCallback(this);
//            requestPermissions();//申请权限
//
//            mqttTools.connect(new IMqttActionListener() {
//                @Override
//                public void onSuccess(IMqttToken iMqttToken) {
//                    mqttTools.subscribe("sl_pst",0);
//                    mqttTools.subscribe("sl_sub",0);
//                    iapp.tw("连接成功");//短暂的时间提示
//                    Log.d("MQTT","连接成功");//调试debug
//                    Log.e("连接成功","mqtt");//输出错误error
//
//                }
//
//                @Override
//                public void onFailure(IMqttToken iMqttToken, Throwable throwable) {
//                    iapp.tw("连接失败");
//                    Log.d("MQTT","连接失败");
//                }
//            });
//            binding.lightOnButton.setOnClickListener(v->light_on());//打开灯光
//            binding.lightOffButton.setOnClickListener(v->light_off());
//
//            binding.ventilateOnButton.setOnClickListener(v->ventilation_on());//打开通风
//            binding.ventilateOffButton.setOnClickListener(v->ventilation_off());
//
//
//            binding.airOnButton.setOnClickListener(v->air_on());//打开空调
//            binding.airOffButton.setOnClickListener(v->air_off());
//
//            binding.curtainOnButton.setOnClickListener(v->curtaion_on());//打开窗帘
//            binding.curtaionOffButton.setOnClickListener(v->curtaion_off());
//
//
//            binding.confirmButton.setOnClickListener(v->set_confirm());
//            // 开始定期获取网络时间
//            startNetworkTimeUpdates();
//
//        }
//        private void startNetworkTimeUpdates() {
//            timeUpdateRunnable = new Runnable() {
//                @Override
//                public void run() {
//                    new Thread(() -> {
//                        try {
//                            URL url = new URL(TIME_API_URL);
//                            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                            connection.setRequestMethod("GET");
//                            connection.setConnectTimeout(3000);
//                            connection.setReadTimeout(3000);
//
//                            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
//                                BufferedReader reader = new BufferedReader(
//                                        new InputStreamReader(connection.getInputStream()));
//                                StringBuilder response = new StringBuilder();
//                                String line;
//                                while ((line = reader.readLine()) != null) {
//                                    response.append(line);
//                                }
//                                reader.close();
//
//                                JSONObject json = new JSONObject(response.toString());
//                                String datetime = json.getString("datetime");
//
//                                // 使用Asia/Shanghai时区解析API返回的时间
//                                SimpleDateFormat apiFormat = new SimpleDateFormat(
//                                        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault());
//                                apiFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//                                Date date = apiFormat.parse(datetime);
//
//                                // 使用相同的Asia/Shanghai时区显示时间
//                                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//                                timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//                                SimpleDateFormat dateFormat = new SimpleDateFormat(
//                                        "yyyy年MM月dd日 EEE", Locale.CHINA);
//                                dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//
//                                final String timeStr = timeFormat.format(date);
//                                final String dateStr = dateFormat.format(date);
//
//                                runOnUiThread(() -> {
//                                    binding.timeText.setText(timeStr);
//                                    binding.timeText.append(" (本地时间)");
//                                    binding.dateText.setText(dateStr);
//                                });
//                            }
//                            connection.disconnect();
//                        } catch (Exception e) {
//                            Log.e(TAG, "获取时间失败", e);
//                            // 失败时使用系统时间作为后备
//                            updateWithSystemTime();
//                        }
//
//                        // 每分钟更新一次
//                        timeUpdateHandler.postDelayed(this, 60000);
//                    }).start();
//                }
//            };
//            timeUpdateHandler.post(timeUpdateRunnable);
//        }
//
//        private void updateWithSystemTime() {
//            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
//            timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy年MM月dd日 EEE", Locale.CHINA);
//            dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
//            Date now = new Date();
//
//            runOnUiThread(() -> {
//                binding.timeText.setText(timeFormat.format(now));
//                binding.dateText.setText(dateFormat.format(now));
//            });
//        }
//
//        @Override
//        protected void onDestroy() {
//            super.onDestroy();
//            // 移除定时更新
//            if (timeUpdateHandler != null && timeUpdateRunnable != null) {
//                timeUpdateHandler.removeCallbacks(timeUpdateRunnable);
//            }
//        }
//
//        private void light_on(){
//
//            int command=1;
//            try{
//                JSONObject jsonCommand=new JSONObject();
//                jsonCommand.put("light",command);
//                mqttTools.publish("sl_sub",jsonCommand.toString());
//                iapp.tw("已经发送");
//
//
//            }catch(JSONException e){
//                iapp.tw("命令发送失败");
//            }
//        }
//
//
//        private void light_off(){
//            int command=0;
//            try{
//                JSONObject jsonCommand=new JSONObject();
//                jsonCommand.put("light",command);
//                mqttTools.publish("sl_sub",jsonCommand.toString());
//                iapp.tw("已经发送");
//            }catch (JSONException e){
//                Log.e("MQTT","失败",e);
//                iapp.tw("发送失败");
//            }
//        }
//
//        private void ventilation_on(){//打开通风
//            int command=1;
//            try{
//                JSONObject jsonCommand=new JSONObject();
//                jsonCommand.put("ventilation",command);
//                mqttTools.publish("sl_sub",jsonCommand.toString());
//                iapp.tw("已经发送");
//            }catch (JSONException e){
//                Log.e("mqtt","error",e);
//            }
//        }
//
//        private void ventilation_off(){//关闭通风
//            int command=0;
//            try{
//                JSONObject jsonCommand=new JSONObject();
//                jsonCommand.put("ventilation",command);
//                mqttTools.publish("sl_sub",jsonCommand.toString());
//                iapp.tw("已经发送");
//            }catch (JSONException e){
//                Log.e("mqtt","error",e);
//            }
//        }
//
//    private void air_on(){//打开空调
//        int command=1;
//        try{
//            JSONObject jsonCommand=new JSONObject();
//            jsonCommand.put("air",command);
//            mqttTools.publish("sl_sub",jsonCommand.toString());
//            iapp.tw("已经发送");
//        }catch (JSONException e){
//            Log.e("mqtt","error",e);
//        }
//    }
//
//    private void air_off(){//关闭空调
//        int command=0;
//        try{
//            JSONObject jsonCommand=new JSONObject();
//            jsonCommand.put("air",command);
//            mqttTools.publish("sl_sub",jsonCommand.toString());
//            iapp.tw("已经发送");
//        }catch (JSONException e){
//            Log.e("mqtt","error",e);
//        }
//    }
//
//    private void curtaion_on(){//打开窗帘
//        int command=1;
//        try{
//            JSONObject jsonCommand=new JSONObject();
//            jsonCommand.put("curtaion",command);
//            mqttTools.publish("sl_sub",jsonCommand.toString());
//            iapp.tw("已经发送");
//        }catch (JSONException e){
//            Log.e("mqtt","error",e);
//        }
//    }
//
//    private void curtaion_off(){//关闭窗帘
//        int command=0;
//        try{
//            JSONObject jsonCommand=new JSONObject();
//            jsonCommand.put("curtaion",command);
//            mqttTools.publish("sl_sub",jsonCommand.toString());
//        }catch (JSONException e){
//            Log.e("mqtt","error",e);
//        }
//    }
//
//
//
//        private void set_water_pump(){
//            isDeviceOn1=!isDeviceOn1;
//            int command=isDeviceOn1 ? 1:0;
//            try{
//                JSONObject jsonCommand=new JSONObject();
//                jsonCommand.put("water_pump",command);
//                mqttTools.publish("sl_sub",jsonCommand.toString());
//                iapp.tw("已经发送");
//                runOnUiThread(()->{
//                    binding.button5.setText(isDeviceOn1 ? "关门":"开门");
//                    iapp.tw("已发送"+command);
//
//                });
//
//            }catch(JSONException e){
//                Log.e("MQTT","命令构造失败",e);
//                iapp.tw("命令发送失败");
//            }
//        }
//
//        private void set_fan(){
//            isDeviceOn2=!isDeviceOn2;
//            int command=isDeviceOn2 ? 1:0;
//            try{
//                JSONObject jsonCommand=new JSONObject();
//                jsonCommand.put("fan",command);
//                mqttTools.publish("sl_sub",jsonCommand.toString());
//                iapp.tw("已经发送");
//                runOnUiThread(()->{
//                    binding.button6.setText(isDeviceOn2 ? "关闭风扇":"打开风扇");
//                    iapp.tw("已发送"+command);
//
//                });
//
//            }catch(JSONException e){
//                Log.e("MQTT","命令构造失败",e);
//                iapp.tw("命令发送失败");
//            }
//        }
//        private void set_confirm(){
//            isDeviceOn3=!isDeviceOn3;
//            int command=isDeviceOn3 ? 1:0;
//    //        String data1=binding.temperatureLow.getText().toString(); //拿到字符串
//    //        String data2=binding.temperatureHigh.getText().toString();
//    //        String data3=binding.humidityLow.getText().toString();
//    //        String data4=binding.humidityHigh.getText().toString();
//
//
//            int wendu_l=Integer.parseInt(binding.temperatureLow.getText().toString().trim());
//            int wendu_h=Integer.parseInt(binding.temperatureHigh.getText().toString().trim());
//
//            int shidu_l=Integer.parseInt(binding.humidityLow.getText().toString().trim());
//            int shidu_h=Integer.parseInt(binding.humidityHigh.getText().toString().trim());
//
//
//
//            try{
//                JSONObject jsonCommand=new JSONObject();
//                jsonCommand.put("temp_l",wendu_l);
//                jsonCommand.put("temp_h",wendu_h);
//
//                jsonCommand.put("humi_l",shidu_l);
//                jsonCommand.put("humi_h",shidu_h);
//
//                mqttTools.publish("sl_sub",jsonCommand.toString());
//                iapp.tw("已经发送");
//                runOnUiThread(()->{
//                    //binding.confirmButton.setText(isDeviceOn3 ? "已经发送":"打开风扇");
//                    iapp.tw("已发送"+command);
//
//                });
//
//            }catch(JSONException e){
//                Log.e("MQTT","命令构造失败",e);
//                iapp.tw("命令发送失败");
//            }
//        }
//
//
//        @Override
//        public void onPointerCaptureChanged(boolean hasCapture) {
//            super.onPointerCaptureChanged(hasCapture);
//        }
//
//        @Override
//        public void onDisconnected(Throwable throwable) {
//            runOnUiThread(()->{
//                iapp.tw("MQTT连接已断开");
//            });
//        }
//
//        @Override
//        public void onMessage(String s, MqttMessage mqttMessage) throws Exception {
//            if(s.equals("sl_pst") && mqttMessage.isJSONObject()){
//                JSONObject data=mqttMessage.getJSONObject();
//                runOnUiThread(()->{
//                    try{
//                        if(data.has("temperature")){
//                            binding.temperatureText.setText(data.getString("temperature")+"℃");
//                        }
//                        if(data.has("humidity")){
//                            binding.humidityText.setText(data.getString("humidity")+"%");
//                        }
//                        if(data.has("light")){
//                            binding.lightText.setText(data.getString("light")+"%");
//                        }
//
//                        if(data.has("fire"))
//                        {
//                            binding.fireText.setText(data.getString("fire")+"%");
//                        }
//                        if(data.has("smoke"))
//                        {
//                            binding.smokeText.setText(data.getString("smoke")+"%");
//                        }
//
//
//
//
//                    }catch(JSONException e){
//                        Log.e("UI","数据解析失败",e);
//                    }
//                });
//            }
//
//        }
//
//
//        @Override
//        public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {
//
//        }
//}