package com.seamfix.mypal.models;

public class MessageModel {
    private String name,date,message,messageController;

    public MessageModel(String name, String date, String message, String messageController) {
        this.name = name;
        this.date = date;
        this.message = message;
        this.messageController = messageController;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }
}
