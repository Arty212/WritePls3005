package com.brks.writepls.ToDoList;

import java.util.HashMap;
import java.util.Map;

public class ToDo {
    private String title;
    private int importance;
    private String key;

    public ToDo() {
    }

    public ToDo(String title, int importance, String key) {
        this.title = title;
        this.importance = importance;
        this.key = key;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getImportance() {
        return importance;
    }

    public void setImportance(int importance) {
        this.importance = importance;
    }

    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("title", title);
        result.put("importance",importance);
        result.put("key",key);


        return result;
    }

}
