package com.hilma.plugin;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.Objects;

@CapacitorPlugin(
        name = "HilmaPushNotifications",
        permissions = @Permission(strings = {Manifest.permission.POST_NOTIFICATIONS, Manifest.permission.ACCESS_NOTIFICATION_POLICY, Manifest.permission.MODIFY_AUDIO_SETTINGS, Manifest.permission.VIBRATE})
)
public class HilmaPushNotificationsPlugin extends Plugin {

    private final String Tag = "HilmaPushNotifications";

    private NotificationSettings settings;
    private PastNotificationsStore store;

    @Override
    public void load() {
        super.load();
        settings = new NotificationSettings(getActivity().getApplicationContext());
        store = new PastNotificationsStore(getActivity().getApplicationContext());
        settings.init(this);

        //start the service that extends firebase
        Context context = getActivity().getApplicationContext();
        Intent serviceIntent = new Intent(context, ModifiedFirebaseService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(serviceIntent);
        } else {
            context.startService(serviceIntent);
        }
    }

    @PluginMethod()
    public void setSettings(PluginCall call) {
        JSObject obj = call.getData();

        try {
            settings.setAll(obj);
        } catch (Exception e) {
            call.reject("Must provide all settings to set settings");
            return;
        }
        call.resolve();
    }

    @PluginMethod()
    public void setSetting(PluginCall call) {
        JSObject data = call.getData();

        if (!data.has("key")) {
            call.reject("Must provide a key");
            return;
        }

        if (!data.has("value")) {
            call.reject("Must provide a value");
            return;
        }

        String key = call.getString("key");

        switch (Objects.requireNonNull(key)) {
            case NotificationSettings.volumeKey -> {
                int volume;
                try {
                    volume = data.getInt("value");
                } catch (JSONException e) {
                    call.reject("Invalid volume value");
                    return;
                }
                settings.setVolume(volume);
            }
            case NotificationSettings.ringtoneKey -> {
                String ringtone = data.getString("value");
                settings.setRingtone(ringtone);
            }
            case NotificationSettings.overrideSilentKey -> {
                boolean overrideSilent = Boolean.TRUE.equals(data.getBoolean("value", false));
                settings.setOverrideSilent(overrideSilent);
            }
            default -> {
                call.reject("Must provide a valid key... got: " + key);
                return;
            }
        }

        call.resolve();
    }

    @PluginMethod()
    public void getSettings(PluginCall call) {
        call.resolve(settings.getAll().toJSObject());
    }

    @PluginMethod()
    public void getNotifications(PluginCall call) {
        JSObject ret = new JSObject();
        JSArray notificationsRet = new JSArray();
        ArrayList<PastNotificationsStore.Notification> notifications = store.getAllNotifications();
        for (int i = 0; i < notifications.size(); i++) {
            PastNotificationsStore.Notification notification = notifications.get(i);
            notificationsRet.put(notification.toJSObject());
        }
        ret.put("notifications", notificationsRet);
        call.resolve(ret);
    }

    @PluginMethod()
    public void clearStoredNotifications(PluginCall call) {
        store.clearAll();
        call.resolve();
    }
}
