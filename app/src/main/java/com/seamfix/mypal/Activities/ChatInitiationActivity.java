package com.seamfix.mypal.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.seamfix.mypal.R;
import com.seamfix.mypal.helper.Utils;

import java.util.ArrayList;

public class ChatInitiationActivity extends Activity {
    private EditText nameEditText, userIdEditText;
    private String nameString, userIdString;
    private Button initiateChatButton;
    private Spinner chatTypeSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initiate_chat);
        initialisation();
        populateChatTypeSpinner();
        listenerMethod();
    }

    private void initialisation() {
        nameEditText = (EditText) findViewById(R.id.edt_name);
        userIdEditText = (EditText) findViewById(R.id.edt_userId);
        initiateChatButton = (Button) findViewById(R.id.btn_chat_initiation);
        chatTypeSpinner = (Spinner) findViewById(R.id.spinner_chat_type);
    }

    private void listenerMethod() {
        initiateChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nameString = nameEditText.getText().toString();
                userIdString = userIdEditText.getText().toString();
                if (inputIsValidate()) {
                    transitionToChatActivity(nameString, userIdString);
                }

            }
        });

        chatTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nameEditText.setText("");
                userIdEditText.setText("");
                if (position == 1) userIdEditText.setHint("Input a unique ID");
                else if (position == 2) userIdEditText.setHint("input your friend's unique ID");

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private boolean inputIsValidate() {

        if (chatTypeSpinner.getSelectedItemPosition() == 0) {
            Toast.makeText(ChatInitiationActivity.this, "Kindly select your chat type", Toast.LENGTH_LONG).show();
            return false;
        }
        if (nameString.isEmpty()) {
            nameEditText.setError("Please input your name");
            return false;
        }
        if (userIdString.isEmpty()) {
            userIdEditText.setError("Please input the Id");
            return false;
        }

        return true;
    }

    private void transitionToChatActivity(String nameString, String userIdString) {
        if(!Utils.isNetworkAvailable(ChatInitiationActivity.this)){
            Toast.makeText(this, "Please check your internet Connection and try again", Toast.LENGTH_LONG).show();
            return;
        }
        Intent intent = new Intent(ChatInitiationActivity.this, ChatActivity.class);
        intent.putExtra("name", nameString);
        intent.putExtra("userId", userIdString);
        startActivity(intent);
    }

    private void populateChatTypeSpinner() {
        ArrayList<String> spinnerContentArray = new ArrayList<>();
        spinnerContentArray.add("Select Chat Type");
        spinnerContentArray.add("Initiate Chat");
        spinnerContentArray.add("Join Chat");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(ChatInitiationActivity.this, R.layout.chat_type_spinner_item, R.id.name, spinnerContentArray);
        dataAdapter.setDropDownViewResource(R.layout.chat_type_spinner_item);
        chatTypeSpinner.setAdapter(dataAdapter);
    }

}
