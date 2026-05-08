package com.example.a6yue9hao_app;

import android.util.Log;

import com.example.a6yue9hao_app.databinding.ActivityControlBinding;
import com.example.a6yue9hao_app.databinding.ActivityThresholdBinding;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import simple.app.SimpleActivity;
import simple.emqx.MqttMessage;
import simple.emqx.MqttTools;
import simple.emqx.lang.IMqttCallBack;

public class AcitivityYuzhi extends SimpleActivity implements IMqttCallBack {
    private ActivityThresholdBinding binding;

    private boolean isDeviceOn =false,isDeviceOn1=false,isDeviceOn2=false,isDeviceOn3=false;//设备状态
    MqttTools mqttTools;
    @Override
    protected void onCreate() {
        binding = ActivityThresholdBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mqttTools =new MqttTools();
        mqttTools.setHostUrl("tcp://broker.emqx.io");
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

        binding.confirmButton.setOnClickListener(v->set_confirm());
        binding.backButton.setOnClickListener(v->{
            startActivity(TeamActivity.class);
            finish();
        });
    }


    private void set_confirm(){
        isDeviceOn3=!isDeviceOn3;
        int command=isDeviceOn3 ? 1:0;
        //        String data1=binding.temperatureLow.getText().toString(); //拿到字符串
        //        String data2=binding.temperatureHigh.getText().toString();
        //        String data3=binding.humidityLow.getText().toString();
        //        String data4=binding.humidityHigh.getText().toString();


        int wendu_l=Integer.parseInt(binding.temperatureLow.getText().toString().trim());
        int wendu_h=Integer.parseInt(binding.temperatureHigh.getText().toString().trim());

        int shidu_l=Integer.parseInt(binding.humidityLow.getText().toString().trim());
        int shidu_h=Integer.parseInt(binding.humidityHigh.getText().toString().trim());



        try{
            JSONObject jsonCommand=new JSONObject();
            jsonCommand.put("temp_l",wendu_l);
            jsonCommand.put("temp_h",wendu_h);

            jsonCommand.put("humi_l",shidu_l);
            jsonCommand.put("humi_h",shidu_h);

            mqttTools.publish("sl_sub",jsonCommand.toString());
            iapp.tw("已经发送");
            runOnUiThread(()->{
                //binding.confirmButton.setText(isDeviceOn3 ? "已经发送":"打开风扇");
                iapp.tw("已发送"+command);

            });

        }catch(JSONException e){
            Log.e("MQTT","命令构造失败",e);
            iapp.tw("命令发送失败");
        }
    }


//    private void set_water_pump(){
//        isDeviceOn1=!isDeviceOn1;
//        int command=isDeviceOn1 ? 1:0;
//        try{
//            JSONObject jsonCommand=new JSONObject();
//            jsonCommand.put("water_pump",command);
//            mqttTools.publish("sl_sub",jsonCommand.toString());
//            iapp.tw("已经发送");
//            runOnUiThread(()->{
//                binding.button5.setText(isDeviceOn1 ? "关门":"开门");
//                iapp.tw("已发送"+command);
//
//            });
//
//        }catch(JSONException e){
//            Log.e("MQTT","命令构造失败",e);
//            iapp.tw("命令发送失败");
//        }
//    }

//    private void set_fan(){
//        isDeviceOn2=!isDeviceOn2;
//        int command=isDeviceOn2 ? 1:0;
//        try{
//            JSONObject jsonCommand=new JSONObject();
//            jsonCommand.put("fan",command);
//            mqttTools.publish("sl_sub",jsonCommand.toString());
//            iapp.tw("已经发送");
//            runOnUiThread(()->{
//                binding.button6.setText(isDeviceOn2 ? "关闭风扇":"打开风扇");
//                iapp.tw("已发送"+command);
//
//            });
//
//        }catch(JSONException e){
//            Log.e("MQTT","命令构造失败",e);
//            iapp.tw("命令发送失败");
//        }
//    }


    @Override
    public void onDisconnected(Throwable throwable) {

    }

    @Override
    public void onMessage(String s, MqttMessage mqttMessage) throws Exception {

    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

    }
}
