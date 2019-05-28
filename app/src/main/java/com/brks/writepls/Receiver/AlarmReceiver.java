package com.brks.writepls.Receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.brks.writepls.Reminder.ReminderNotification;

public class AlarmReceiver extends BroadcastReceiver {

    int pos = 1;
    static String Text;
    @Override
    public void onReceive(Context context, Intent intent) {
        ReminderNotification.notify(context, Text, pos);
        pos++;
    }

    public static void textNot(String text){
        Text = text;
    }

}
