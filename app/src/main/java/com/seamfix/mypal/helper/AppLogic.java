package com.seamfix.mypal.helper;

import android.content.Context;
import android.widget.Toast;

import com.seamfix.mypal.models.MessageModel;
import com.seamfix.mypal.Activities.ChatActivity;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.seamfix.mypal.Constants.delimiter;
import static com.seamfix.mypal.Constants.qos;
import static com.seamfix.mypal.Activities.ChatActivity.adapter;

public class AppLogic {
    private boolean isSubscribeToTopic = false;
    public List<MessageModel> chatList = new ArrayList<MessageModel>();
    public AppLogic() {
    }

    public void connectToServer(final Context context, final MqttAndroidClient client, final String topicString) {
        IMqttToken token;
        try {
            token = client.connect();
            token.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    ChatActivity.clientIsConnectedBoolean = true;
                    subscribeToTopic(context, client, topicString, qos);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public boolean subscribeToTopic(final Context context, final MqttAndroidClient client, String topicString, int qos) {
        IMqttToken subToken;
        try {
            subToken = client.subscribe(topicString, qos);
            subToken.setActionCallback(new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    isSubscribeToTopic = true;
                    Toast.makeText(context, "You are now connected!", Toast.LENGTH_SHORT).show();
                    fetchMessage(client);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken,
                                      Throwable exception) {
                    isSubscribeToTopic = false;
                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
        return isSubscribeToTopic;
    }

    public boolean publishMessage(MqttAndroidClient client, String topicString, String messageString) {
        boolean isPublished;
        byte[] encodedPayload = new byte[0];
        try {
            encodedPayload = messageString.getBytes("UTF-8");
            MqttMessage message = new MqttMessage(encodedPayload);
            message.setQos(qos);
            client.publish(topicString, message);
            isPublished = true;
        } catch (UnsupportedEncodingException | MqttException e) {
            e.printStackTrace();
            isPublished = false;
        }
        return isPublished;
    }

    public void fetchMessage(MqttAndroidClient client) {
        client.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                String fetchedMessage = message.toString();
                messageManipulation(fetchedMessage);
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
            }
        });
    }

    void messageManipulation(String fetchedMessage) {
        StringTokenizer tokens = new StringTokenizer(fetchedMessage, delimiter);
        String nameString = tokens.nextToken().trim();
        String dateString = tokens.nextToken().trim();
        String messageString = tokens.nextToken().trim();
        String messageControllerString = tokens.nextToken().trim();

        //messageControllerString is fetched along every message to state the intent of the message.
        if (messageControllerString.equalsIgnoreCase("delete")) {
            for (int position = 0; position < chatList.size(); position++) {
                if (chatList.get(position).getDate().contains(dateString)) {
                    chatList.remove(position);
                }
            }
        } else if (messageControllerString.equalsIgnoreCase("edit")) {
            for (int position = 0; position < chatList.size(); position++) {
                if (chatList.get(position).getDate().contains(dateString)) {
                    MessageModel messageModel = new MessageModel(nameString, dateString, messageString, messageControllerString);
                    chatList.set(position, messageModel);
                }
            }

        } else if (messageControllerString.equalsIgnoreCase("display")) {
            MessageModel messageModel = new MessageModel(nameString, dateString, messageString, messageControllerString);
            chatList.add(messageModel);
        }
        adapter.notifyDataSetChanged();
    }
}
