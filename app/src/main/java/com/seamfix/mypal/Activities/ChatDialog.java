package com.seamfix.mypal.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.seamfix.mypal.R;
import com.seamfix.mypal.helper.Utils;
import com.seamfix.mypal.helper.AppLogic;

import static com.seamfix.mypal.Activities.ChatActivity.userIdString;

public class ChatDialog extends Activity {
    private String nameString, messageString, dateString;
    private Button deleteButton, editButton;
    AppLogic appLogic = new AppLogic();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_dialog);
        getIntentMethod();
        initialisation();
        listenerMethod();
    }

    void getIntentMethod() {
        Intent intent = getIntent();
        nameString = intent.getStringExtra("name");
        dateString = intent.getStringExtra("date");
        messageString = intent.getStringExtra("message");
        userIdString = intent.getStringExtra("userId");
    }

    void initialisation() {
        deleteButton = (Button) findViewById(R.id.btn_delete);
        editButton = (Button) findViewById(R.id.btn_edit);
    }

    void handleMessageDelete() {
        if (null != ChatActivity.client)
            appLogic.publishMessage(ChatActivity.client, userIdString, Utils.messageFormatter(nameString, dateString, messageString, "delete"));
        finish();
    }

    void listenerMethod() {
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handleMessageDelete();
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatActivity.editMessageString = messageString;
                ChatActivity.editMessageDateString = dateString;
                finish();
            }
        });
    }
}
