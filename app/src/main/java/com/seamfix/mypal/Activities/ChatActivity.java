package com.seamfix.mypal.Activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.seamfix.mypal.R;
import com.seamfix.mypal.helper.Utils;
import com.seamfix.mypal.helper.AppLogic;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.MqttClient;

public class ChatActivity extends Activity {

    public static boolean clientIsConnectedBoolean = false;
    public static String editMessageString = "", editMessageDateString = "";
    private ListView chatListView;
    private EditText chatEditText;
    private Button chatSubmitButton;
    private boolean block_back_pressed =  true;
    public static String nameString, userIdString;
    public static MqttAndroidClient client;
    public static ChatAdapter adapter;
    AppLogic appLogic = new AppLogic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getIntentMethod();
        connectToServer(userIdString);
        initialisation();
        setListViewAdapter();
        listenerMethod();
    }

    void getIntentMethod() {
        Intent intent = getIntent();
        nameString = intent.getStringExtra("name");
        userIdString = intent.getStringExtra("userId");
    }

    void initialisation() {
        chatListView = (ListView) findViewById(R.id.chat_listview);
        chatEditText = (EditText) findViewById(R.id.edt_message);
        chatSubmitButton = (Button) findViewById(R.id.btn_message_submit);
    }

    void setListViewAdapter() {
        adapter = new ChatAdapter(this, appLogic.chatList);
        chatListView.setAdapter(adapter);
    }

    void listenerMethod() {
        chatSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String messageString = chatEditText.getText().toString().trim();
                if (!clientIsConnectedBoolean) {
                    Toast.makeText(ChatActivity.this, "You are not connected yet!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (messageString.isEmpty()) {
                    chatEditText.setError("This field can not be empty!");
                    return;
                }

                if (editMessageString.equalsIgnoreCase("")) {
                    //editMessageString is used to capture the message to edit from the chatDialogActivity. If empty,its means that the message in the chatEditText is new post hence display purpose
                    String message = Utils.messageFormatter(nameString, Utils.currentDate(), messageString, "display");
                    if (appLogic.publishMessage(client, userIdString, message))
                        chatEditText.setText("");

                } else if (!editMessageString.equalsIgnoreCase("")) {
                    //From chatDialogActivity, the chatEditText has been filled with  editMessageString hence it is not not a new post but a text to edit. Hence an edit controller is published.
                    String message = Utils.messageFormatter(nameString, editMessageDateString, messageString, "edit");
                    if (appLogic.publishMessage(client, userIdString, message)) {
                        chatEditText.setText("");
                        editMessageString = "";
                        chatSubmitButton.setText("send");
                    }
                }
            }

        });
    }

    public void connectToServer(final String topicString) {
        Toast.makeText(ChatActivity.this, "Hold on while we connect you!!", Toast.LENGTH_LONG).show();

        String clientId = MqttClient.generateClientId();
        client = new MqttAndroidClient(ChatActivity.this, "tcp://broker.hivemq.com:1883", clientId);
        appLogic.connectToServer(ChatActivity.this, client, topicString);
    }

    public void endActivityDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ChatActivity.this);
        builder.setMessage("Would you like to end the chat?");

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
              finish();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!editMessageString.equalsIgnoreCase("")) {
            chatEditText.setText(editMessageString);
            chatSubmitButton.setText("Edit");
        }
    }

    @Override
    public void onBackPressed() {
        if (block_back_pressed) {
           endActivityDialog();
        } else {
            super.onBackPressed();
        }

    }
}
