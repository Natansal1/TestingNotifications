package com.hilma.plugin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;

import com.getcapacitor.JSObject;
import com.google.gson.Gson;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;

public class PastNotificationsStore {

    private static final String PREFS_NAME = "HilmaPushNotificationsPrefs";
    private static final String PREFS_KEY = "NotificationStore";
    private final SharedPreferences preferences;
    private final Gson gson = new Gson();
    public PastNotificationsStore(Context context) {
        preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String storedData = preferences.getString(PREFS_KEY, null);
        if (storedData == null) {
            storeData(new ArrayList<Notification>());
        }
    }

    private void storeData(ArrayList<Notification> notifications) {
        SharedPreferences.Editor editor = preferences.edit();
        String json = gson.toJson(notifications);
        editor.putString(PREFS_KEY, json);
        editor.apply();
    }

    private ArrayList<Notification> getData() {
        String json = preferences.getString(PREFS_KEY, null);
        if (json == null) {
            return new ArrayList<>();
        }
        Notification[] array = gson.fromJson(json, Notification[].class);
        return new ArrayList<>(Arrays.asList(array));
    }

    public void save(Notification notification) {
        ArrayList<Notification> notifications = getData();
        notifications.add(notification);
        storeData(notifications);
    }

    public ArrayList<Notification> getAllNotifications() {
        return getData();
    }

    public void clearAll() {
        storeData(new ArrayList<Notification>());
    }

    public class Notification {
        private final long timestamp;
        private final JSObject data;

        public Notification(JSObject data) {
            this.data = data;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.timestamp = Instant.now().toEpochMilli();
            } else {
                this.timestamp = System.currentTimeMillis();
            }
        }

        public Notification(long timestamp, JSObject data) {
            this.timestamp = timestamp;
            this.data = data;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public JSObject getData() {
            return data;
        }

        public JSObject toJSObject() {
            JSObject ret = new JSObject();
            ret.put("timestamp", this.timestamp);
            ret.put("data", this.data);
            return ret;
        }

        public void save() {
            PastNotificationsStore.this.save(this);
        }
    }
}
