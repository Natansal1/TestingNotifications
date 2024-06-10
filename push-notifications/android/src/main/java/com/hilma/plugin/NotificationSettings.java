package com.hilma.plugin;

import android.content.Context;
import android.content.SharedPreferences;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginConfig;
import com.google.gson.Gson;

import org.json.JSONException;

public class NotificationSettings {
    public static final String ringtoneKey = "ringtoneFileName";
    public static final String volumeKey = "volume";
    public static final String overrideSilentKey = "overrideSilentEnabled";
    public static final String vibrationPatternKey = "vibrationPattern";
    public static final String lightsKey = "lights";
    private static final String PREFS_NAME = "HilmaPushNotificationsPrefs";
    private final SharedPreferences preferences;

    public NotificationSettings(Context context) {
        this.preferences = context.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }

    public void init(Plugin plugin) {
        PluginConfig config = plugin.getConfig();
        if (!preferences.contains(ringtoneKey)) {
            String ringtone = config.getString(ringtoneKey);
            setRingtone(ringtone == null ? "default" : ringtone);
        }
        if (!preferences.contains(volumeKey)) {
            int volume = config.getInt(volumeKey, 100);
            setVolume(volume);
        }
        if (!preferences.contains(overrideSilentKey)) {
            boolean overrideSilent = config.getBoolean(overrideSilentKey, false);
            setOverrideSilent(overrideSilent);
        }
        String[] lights = config.getArray(lightsKey, new String[0]);
        String[] vibrationPattern = config.getArray(vibrationPatternKey, new String[0]);
        setLightsVibration(lights, vibrationPattern);
    }

    public int getVolume() {
        return preferences.getInt(volumeKey, 100);
    }

    public void setVolume(int volume) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(volumeKey, volume);
        editor.apply();
    }

    public String getRingtone() {
        return preferences.getString(ringtoneKey, "default");
    }

    public void setRingtone(String ringtone) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(ringtoneKey, ringtone);
        editor.apply();
    }

    public boolean getOverrideSilent() {
        return Boolean.TRUE.equals(preferences.getBoolean(overrideSilentKey, false));
    }

    public void setOverrideSilent(Boolean overrideSilent) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(overrideSilentKey, overrideSilent);
        editor.apply();
    }

    private void setLightsVibration(String[] lights, String[] vibration) {
        Gson gson = new Gson();
        SharedPreferences.Editor editor = preferences.edit();
        String lightsJson = gson.toJson(lights);
        String vibrationJson = gson.toJson(vibration);
        editor.putString(lightsKey, lightsJson);
        editor.putString(vibrationPatternKey, vibrationJson);
        editor.apply();
    }

    public int[] getLights() {
        try {
            String json = preferences.getString(lightsKey, null);
            if (json == null) return new int[0];
            Gson gson = new Gson();
            String[] lights = gson.fromJson(json, String[].class);
            int[] ret = new int[Math.min(lights.length, 3)];
            for (int i = 0; i < Math.min(lights.length, 3); i++) {
                ret[i] = Integer.parseInt(lights[i], i == 0 ? 16 : 10);
            }
            return ret;
        } catch (NumberFormatException e) {
            return new int[0];
        }
    }

    public boolean isValidLights(int[] lights) {
        return lights.length == 3;
    }

    public long[] getVibrationPattern() {
        try {
            String json = preferences.getString(vibrationPatternKey, null);
            if (json == null) return new long[0];
            Gson gson = new Gson();
            String[] vibration = gson.fromJson(json, String[].class);
            long[] ret = new long[Math.min(vibration.length, 3)];
            for (int i = 0; i < Math.min(vibration.length, 3); i++) {
                ret[i] = Long.parseLong(vibration[i]);
            }
            return ret;
        } catch (NumberFormatException e) {
            return new long[0];
        }
    }

    public SettingsObject getAll() {
        return new SettingsObject();
    }

    public void setAll(JSObject obj) throws Exception {
        SettingsObject settings = new SettingsObject(obj);
        settings.save();
    }

    public class SettingsObject {
        private final int volume;
        private final String ringtone;
        private final boolean overrideSilent;

        public SettingsObject() {
            volume = NotificationSettings.this.getVolume();
            ringtone = NotificationSettings.this.getRingtone();
            overrideSilent = NotificationSettings.this.getOverrideSilent();
        }

        public SettingsObject(PluginCall call) throws Exception {
            JSObject obj = call.getData();

            try {
                volume = obj.getInt(volumeKey);
            } catch (JSONException e) {
                throw new Exception("Volume invalid");
            }

            if (!obj.has(ringtoneKey)) throw new Exception("Ringtone must be provided");

            ringtone = obj.getString(ringtoneKey);

            try {
                overrideSilent = obj.getBoolean(overrideSilentKey);
            } catch (JSONException e) {
                throw new Exception("OverrideSilent invalid");
            }
        }

        public SettingsObject(JSObject obj) throws Exception {
            try {
                volume = obj.getInt(volumeKey);
            } catch (JSONException e) {
                throw new Exception("Volume invalid");
            }

            if (!obj.has(ringtoneKey)) throw new Exception("Ringtone must be provided");

            ringtone = obj.getString(ringtoneKey);

            try {
                overrideSilent = obj.getBoolean(overrideSilentKey);
            } catch (JSONException e) {
                throw new Exception("OverrideSilent invalid");
            }
        }

        public SettingsObject(String ringtone, int volume, boolean overrideSilent) {
            this.ringtone = ringtone;
            this.volume = volume;
            this.overrideSilent = overrideSilent;
        }

        public int getVolume() {
            return volume;
        }

        public String getRingtone() {
            return ringtone;
        }

        public boolean getOverrideSilent() {
            return overrideSilent;
        }

        public void save() {
            SharedPreferences.Editor editor = preferences.edit();

            editor.putString(ringtoneKey, ringtone);
            editor.putInt(volumeKey, volume);
            editor.putBoolean(overrideSilentKey, overrideSilent);

            editor.apply();
        }

        public JSObject toJSObject() {
            JSObject ret = new JSObject();

            ret.put(ringtoneKey, ringtone);
            ret.put(volumeKey, volume);
            ret.put(overrideSilentKey, overrideSilent);

            return ret;
        }
    }
}