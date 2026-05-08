package com.example.a6yue9hao_app;

import android.util.Log;

import com.example.a6yue9hao_app.databinding.ActivityControlBinding;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.json.JSONException;
import org.json.JSONObject;

import simple.app.SimpleActivity;
import simple.emqx.MqttMessage;
import simple.emqx.MqttTools;
import simple.emqx.lang.IMqttCallBack;

public class ActivityControl extends SimpleActivity implements IMqttCallBack {
    private ActivityControlBinding binding;
    MqttTools mqttTools;

    @Override
    protected void onCreate() {
        binding = ActivityControlBinding.inflate(getLayoutInflater());
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

        binding.lightOnButton.setOnClickListener(v->light_on());//打开灯光
        binding.lightOffButton.setOnClickListener(v->light_off());

        binding.ventilateOnButton.setOnClickListener(v->ventilation_on());//打开通风
        binding.ventilateOffButton.setOnClickListener(v->ventilation_off());


        binding.airOnButton.setOnClickListener(v->air_on());//打开空调
        binding.airOffButton.setOnClickListener(v->air_off());

        binding.curtainOnButton.setOnClickListener(v->curtaion_on());//打开窗帘
        binding.curtaionOffButton.setOnClickListener(v->curtaion_off());

        binding.jiashiqiOnButton.setOnClickListener(v->jiashiqi_on());//打开加湿器
        binding.jiashiqiOffButton.setOnClickListener(v->jiashiqi_off());

        binding.backButton.setOnClickListener(v->{
            startActivity(TeamActivity.class);
            finish();
        });


    }

    private void light_on(){

        int command=1;
        try{
            JSONObject jsonCommand=new JSONObject();
            jsonCommand.put("light",command);
            mqttTools.publish("sl_sub",jsonCommand.toString());
            iapp.tw("已经发送");


        }catch(JSONException e){
            iapp.tw("命令发送失败");
        }
    }


    private void light_off(){
        int command=0;
        try{
            JSONObject jsonCommand=new JSONObject();
            jsonCommand.put("light",command);
            mqttTools.publish("sl_sub",jsonCommand.toString());
            iapp.tw("已经发送");
        }catch (JSONException e){
            Log.e("MQTT","失败",e);
            iapp.tw("发送失败");
        }
    }

    private void ventilation_on(){//打开通风
        int command=1;
        try{
            JSONObject jsonCommand=new JSONObject();
            jsonCommand.put("ventilation",command);
            mqttTools.publish("sl_sub",jsonCommand.toString());
            iapp.tw("已经发送");
        }catch (JSONException e){
            Log.e("mqtt","error",e);
        }
    }

    private void ventilation_off(){//关闭通风
        int command=0;
        try{
            JSONObject jsonCommand=new JSONObject();
            jsonCommand.put("ventilation",command);
            mqttTools.publish("sl_sub",jsonCommand.toString());
            iapp.tw("已经发送");
        }catch (JSONException e){
            Log.e("mqtt","error",e);
        }
    }

    private void air_on(){//打开空调
        int command=1;
        try{
            JSONObject jsonCommand=new JSONObject();
            jsonCommand.put("air",command);
            mqttTools.publish("sl_sub",jsonCommand.toString());
            iapp.tw("已经发送");
        }catch (JSONException e){
            Log.e("mqtt","error",e);
        }
    }

    private void air_off(){//关闭空调
        int command=0;
        try{
            JSONObject jsonCommand=new JSONObject();
            jsonCommand.put("air",command);
            mqttTools.publish("sl_sub",jsonCommand.toString());
            iapp.tw("已经发送");
        }catch (JSONException e){
            Log.e("mqtt","error",e);
        }
    }

    private void curtaion_on(){//打开窗帘
        int command=1;
        try{
            JSONObject jsonCommand=new JSONObject();
            jsonCommand.put("curtaion",command);
            mqttTools.publish("sl_sub",jsonCommand.toString());
            iapp.tw("已经发送");
        }catch (JSONException e){
            Log.e("mqtt","error",e);
        }
    }

    private void curtaion_off(){//关闭窗帘
        int command=0;
        try{
            JSONObject jsonCommand=new JSONObject();
            jsonCommand.put("curtaion",command);
            mqttTools.publish("sl_sub",jsonCommand.toString());
        }catch (JSONException e){
            Log.e("mqtt","error",e);
        }
    }

    private void jiashiqi_on(){//打开加湿器
        int command=1;
        try{
            JSONObject jsonCommand=new JSONObject();
            jsonCommand.put("jiashiqi",command);
            mqttTools.publish("sl_sub",jsonCommand.toString());
            iapp.tw("已经发送");
        }catch (JSONException e){
            Log.e("mqtt","error",e);
        }
    }

    private void jiashiqi_off(){//关闭加湿器
        int command=0;
        try{
            JSONObject jsonCommand=new JSONObject();
            jsonCommand.put("jiashiqi",command);
            mqttTools.publish("sl_sub",jsonCommand.toString());
        }catch (JSONException e){
            Log.e("mqtt","error",e);
        }
    }

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
