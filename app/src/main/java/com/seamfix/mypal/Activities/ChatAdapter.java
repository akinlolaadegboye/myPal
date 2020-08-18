package com.seamfix.mypal.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.seamfix.mypal.Constants;
import com.seamfix.mypal.R;
import com.seamfix.mypal.helper.Utils;
import com.seamfix.mypal.models.MessageModel;

import java.text.SimpleDateFormat;
import java.util.List;

public class ChatAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MessageModel> chatItems;

    public ChatAdapter(Activity activity, List<MessageModel> chatItems) {
        this.activity = activity;
        this.chatItems = chatItems;
    }

    @Override
    public int getCount() {
        return chatItems.size();
    }

    @Override
    public Object getItem(int location) {
        return chatItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MMM-yyyy");
        long messageDate = Long.parseLong(chatItems.get(position).getDate());

        String messageString = chatItems.get(position).getMessage();
        String dateString = simpleDateFormat.format(messageDate);
        final String nameString = chatItems.get(position).getName();

        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (nameString.equalsIgnoreCase(ChatActivity.nameString)) {
            convertView = inflater.inflate(R.layout.user_items, null);
        } else {
            convertView = inflater.inflate(R.layout.friend_items, null);
        }


        TextView messageTextView = (TextView) convertView.findViewById(R.id.tvt_message);
        TextView dateTextView = (TextView) convertView.findViewById(R.id.tvt_date);
        TextView nameTextView = (TextView) convertView.findViewById(R.id.tvt_name);

        messageTextView.setText(messageString);
        dateTextView.setText(dateString);
        nameTextView.setText(nameString);

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                if (ChatActivity.nameString.equalsIgnoreCase(nameString) && Utils.timeDifferenceInMinutes(chatItems.get(position).getDate()) <= Constants.chatDeleteAndEditTimeLimit) {
                    Intent intent = new Intent(activity, ChatDialog.class);
                    intent.putExtra("name", chatItems.get(position).getName());
                    intent.putExtra("message", chatItems.get(position).getMessage());
                    intent.putExtra("date", chatItems.get(position).getDate());
                    intent.putExtra("userId", ChatActivity.userIdString);
                    activity.startActivity(intent);
                } else {
                    if (!ChatActivity.nameString.equalsIgnoreCase(nameString)) {
                        Toast.makeText(activity, "You can only edit or delete your message!", Toast.LENGTH_LONG).show();
                    } else if (Utils.timeDifferenceInMinutes(chatItems.get(position).getDate()) > Constants.chatDeleteAndEditTimeLimit) {
                        Toast.makeText(activity, "You can only edit  or delete within 2 minutes of posting!", Toast.LENGTH_LONG).show();
                    }
                }
                return false;
            }
        });
        return convertView;
    }
}