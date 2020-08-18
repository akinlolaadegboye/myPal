package com.seamfix.mypal.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import static com.seamfix.mypal.Constants.delimiter;

public class Utils {

    //messageControllerString is sent along every message to state the intent of the message.
    public static String messageFormatter(String name, String date, String message, String messageController) {
        String formattedMessage = name + delimiter + date + delimiter + message + delimiter + messageController;
        return formattedMessage;
    }

    public static String currentDate() {
        return String.valueOf(System.currentTimeMillis());
    }

    public static long timeDifferenceInMinutes(String messageDateString){
        long messageDate = Long.parseLong(messageDateString);
        long currentDate = System.currentTimeMillis();
        long dateDifference = currentDate -messageDate;
        long minutes=dateDifference/ (60 * 1000) % 60;
        return minutes;
    }

    public static boolean isNetworkAvailable(Context cParam) {
        Context mContext = cParam.getApplicationContext();
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}
