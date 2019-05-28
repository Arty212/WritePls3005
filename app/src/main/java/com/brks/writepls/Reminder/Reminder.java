package com.brks.writepls.Reminder;

import java.util.HashMap;
import java.util.Map;

public class Reminder {
    public Reminder(int hour,int minute, boolean flag, String text, String Key) {
        this.hour = hour;
        this.minute = minute;
        this.flag = flag;
        Text = text;
        this.key = Key;
    }

    public Reminder(){
    }

    private int hour;
    private int minute;
    private boolean flag;
    private String Text;
    private String key;

    public int getHour() {
        return hour;
    }

    public boolean isFlag() {
        return flag;
    }

    public String getText() {
        return Text;
    }

    public void setHour(int time) {
        hour = time;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setText(String text) {
        Text = text;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }


    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("flag", flag);
        result.put("hour",hour);
        result.put("minute",minute);
        result.put("Text",Text);
        result.put("key",key);

        return result;
    }
}