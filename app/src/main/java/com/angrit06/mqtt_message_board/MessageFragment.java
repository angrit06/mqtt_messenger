package com.angrit06.mqtt_message_board;

import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.Calendar;
import java.util.Date;

public class MessageFragment extends Fragment {
    private MessageViewModel mViewModel;
    MessagesList mMessagesList;
    private MessageInput mMessageInput;
    private MessagesListAdapter<Message> adapter;
    String clientId = MqttClient.generateClientId();
    MqttAndroidClient client;

    public static MessageFragment newInstance() {
        return new MessageFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.message_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
    }

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        mMessagesList = new MessagesList(getContext());
        mMessagesList = view.findViewById(R.id.messagesList);
        mMessageInput = view.findViewById(R.id.message);
        startMqtt();

        adapter = new MessagesListAdapter<>("Angrit", null);// we don't need images
        mMessagesList.setAdapter(adapter);

        mMessageInput.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                if (!input.toString().isEmpty()) {
                    Date date = Calendar.getInstance().getTime();
                    User user = new User("Angrit", "gr", null);
                    Message message = new Message("id", input.toString(), date, user);
                    String topic = MainActivity.getTopic();
                    Toast.makeText(getActivity().getApplicationContext(), topic, Toast.LENGTH_SHORT).show();
                    adapter.addToStart(message, true);
                    publishMessage(topic, input.toString());
                    return true;

                }
                return false;
            }
        });
        super.onViewCreated(view, savedInstanceState);

    }

    public void startMqtt() {
        client =
                new MqttAndroidClient(getActivity().getApplicationContext(), "tcp://192.168.2.122:1883",
                        clientId);
        try {
            IMqttToken token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    // We are connected
                    Toast.makeText(getActivity().getApplicationContext(), "mqtt connection runs!", Toast.LENGTH_SHORT).show();
                    subscribeMessage("/mqtt_message_board/response/");
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    // Something went wrong e.g. connection timeout or firewall problems
                    Toast.makeText(getActivity().getApplicationContext(), "mqtt connection failed!", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publishMessage(String topic, String payload) {
        MqttMessage message = new MqttMessage(payload.getBytes());
        try {
            client.publish(topic, message);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribeMessage(String topic) {
        int qos = 1;
        try {
            IMqttToken subToken = client.subscribe(topic, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(getActivity().getApplicationContext(), "subscription was successful!", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(getActivity().getApplicationContext(), "subscription was unsuccessful!", Toast.LENGTH_SHORT).show();

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {

            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                onMessage(message);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {

            }
        });
    }


    private void onMessage(MqttMessage message) {
        String messageContent = message.toString();
        User user = new User("MessageBoard", "gr", null);

        Date date = Calendar.getInstance().getTime();

        String messageToDisplay = messageContent.substring(0, messageContent.length() - 1);

        Message message_r = new Message("id_r", messageToDisplay, date, user);

        adapter.addToStart(message_r, true);


    }


}