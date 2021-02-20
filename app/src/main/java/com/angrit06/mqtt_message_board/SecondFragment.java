package com.angrit06.mqtt_message_board;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class SecondFragment extends Fragment {
    EditText payload;
    MQTTHelper mqttHelper;
    String clientId = MqttClient.generateClientId();
    MqttAndroidClient client;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_second, container, false);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        payload = (EditText) view.findViewById(R.id.payload);
        MainActivity mainActivity = (MainActivity) getActivity();
        String topic = mainActivity.getTopic();
        startMqtt();
        view.findViewById(R.id.backToTopic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NavHostFragment.findNavController(SecondFragment.this)
                        .navigate(R.id.action_SecondFragment_to_FirstFragment);
            }
        });

        view.findViewById(R.id.sendMessage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(payload.getText().toString().isEmpty()){
                    Toast.makeText(getActivity().getApplicationContext(),"Please enter your message!",Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity().getApplicationContext(),payload.getText().toString(),Toast.LENGTH_SHORT).show();
                    publishMessgae(topic,payload.getText().toString());
                }
            }
        });
    }
    public void startMqtt(){
        client =
                new MqttAndroidClient(getActivity().getApplicationContext(), "tcp://raspberrypi:1883",
                        clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(getActivity().getApplicationContext(),"mqtt connection runs!",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(getActivity().getApplicationContext(),"mqtt connection failed!",Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
    public void publishMessgae(String topic, String payload){
        MqttMessage message = new MqttMessage(payload.toString().getBytes());
        try {
            client.publish(topic, message);
        } catch (MqttException e){
            e.printStackTrace();
        }
    }
    public void subscribeMessage(String topic){
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getActivity().getApplicationContext(),"subscription was successful!",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getActivity().getApplicationContext(),"subscription was unsuccessful!",Toast.LENGTH_SHORT).show();

                }
            });
        }catch (MqttException e) {
            e.printStackTrace();
        }
    }
}