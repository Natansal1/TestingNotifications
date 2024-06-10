package com.hilma.plugin;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.capacitorjs.plugins.pushnotifications.MessagingService;
import com.getcapacitor.JSObject;
import com.google.firebase.messaging.RemoteMessage;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;

import java.util.Map;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class ModifiedFirebaseService extends MessagingService {

    public static final String TAG = "HilmaPushNotifications";

    private NotificationSettings settings;

    private PastNotificationsStore store;

    @Override
    public void onCreate() {
        super.onCreate();
        store = new PastNotificationsStore(getApplicationContext());
        NotificationChannelManager.deployOngoingNotification(this);
        settings = new NotificationSettings(getApplicationContext());
        Log.d(TAG, "Initiated firebase interceptor");
    }


    @Override
    public void onMessageReceived(@NotNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Log.d(TAG, "message received!");

        //Retrieve the "should_override_silent" from the notification - set to "yes" or "no"
        Map<String, String> notificationData = remoteMessage.getData();

        //Will be true if the notification is important and the user had set the "override silent mode" to `true`
        boolean overrideSilent = "yes".equals(notificationData.get("should_override_silent")) && settings.getOverrideSilent();

        int notificationId = generateUniqueNotificationId(notificationData.get("notification_id"));

        //If there is no title or text with the notification
        if (!notificationData.containsKey("title") || !notificationData.containsKey("text")) return;


        //Store the notification in the sharedPreferences
        storeNotification(notificationData);

        try {
            fireNotification(notificationData, overrideSilent, notificationId);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void storeNotification(Map<String, String> data) {
        JSObject dataObj = new JSObject();
        for (Map.Entry<String, String> entry : data.entrySet()) {
            dataObj.put(entry.getKey(), entry.getValue());
        }
        PastNotificationsStore.Notification notification = store.new Notification(dataObj);
        notification.save();
    }

    public void fireNotification(Map<String, String> notificationData, boolean overrideSilent, int notificationId) throws JSONException {
        //Get the channel Id to use for the notification
        String ringtone = settings.getRingtone();
        String CHANNEL_ID = NotificationChannelManager.getNotificationChannelId(ringtone);

        Log.d(TAG, CHANNEL_ID);

        //Create the channel to deploy the notification
        NotificationChannelManager.createNotificationChannelIfDoesntExist(getApplicationContext(), ringtone);

        //get the title and body of the notification from the data
        String title = notificationData.get("title");
        String body = notificationData.get("text");

        //Create managers to handle settings and notifications
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Get the maximum volume for the STREAM_RING audio stream
        int maxAlarmVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_ALARM);
        int maxNotificationVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_NOTIFICATION);

        int volume = settings.getVolume();

        // Calculate the desired volume based on the percentage
        int desiredAlarmVolume = (int) (maxAlarmVolume * (volume) / 100.0);
        int desiredNotificationVolume = (int) (maxNotificationVolume * (volume / 100.0));

        //disable the silent mode before firing the notification if we want to override silent mode
        if (overrideSilent)
            this.disableSilentMode(desiredAlarmVolume, desiredNotificationVolume, NotificationChannelManager.getRingtoneLength(getApplicationContext(), ringtone), audioManager, notificationManager);


        //Create an intent to open the app when the user clicks the notification
        Intent intent = new Intent(this, getApplicationContext().getClass());
        intent.setPackage(getPackageName());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);

        //Build the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(NotificationChannelManager.getNotificationIcon(getApplicationContext()))
                .setContentTitle(title)
                .setContentText(body)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setSound(NotificationChannelManager.ringtoneToUri(getApplicationContext(), ringtone))
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent) // Set the pending intent on the builder
                .setAutoCancel(true);// Automatically remove the notification when clicked


        int[] lights = settings.getLights();
        long[] vibration = settings.getVibrationPattern();

        if (settings.isValidLights(lights)) builder.setLights(lights[0], lights[1], lights[2]);
        if (vibration.length > 0) builder.setVibrate(vibration);

        //Fire the notification
        notificationManager.notify(notificationId, builder.build());
    }

    private int generateUniqueNotificationId(String notificationIdValue) {
        int notificationId;
        if (notificationIdValue == null) {
            notificationId = (int) Math.ceil(Math.random() * 10000);
        } else {
            try {
                notificationId = Integer.parseInt(notificationIdValue);
            } catch (NumberFormatException _err) {
                notificationId = (int) Math.ceil(Math.random() * 10000);
            }
        }
        //Generate a random notification Id
        if (notificationId == NotificationChannelManager.ONGOING_NOTIFICATION_ID) notificationId++;
        return notificationId;
    }

    private void disableSilentMode(int desiredAlarmVolume, int desiredNotificationVolume, int length, AudioManager audioManager, NotificationManager notificationManager) {
        if (desiredAlarmVolume == 0) desiredAlarmVolume++;
        if (desiredNotificationVolume == 0) desiredNotificationVolume++;

        // Store the prev modes to restore them after the notification
        int prevRingerMode = audioManager.getRingerMode();
        int prevAlarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        int prevNotificationVolume = audioManager.getStreamVolume(AudioManager.STREAM_NOTIFICATION);
        int prevInterruptionFilter = 1;

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            prevInterruptionFilter = notificationManager.getCurrentInterruptionFilter();
        }


        int finalPrevInterruptionFilter = prevInterruptionFilter;

        try {
            // Set ringer mode to normal
            audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

            // Set the volume to the volume set
            audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, desiredNotificationVolume, 0);
            audioManager.setStreamVolume(AudioManager.STREAM_ALARM, desiredAlarmVolume, 0);

            // Disable DnD mode if the SDK version is above 23
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_ALL);
            }
        } catch (SecurityException e) {
            Log.e(TAG, "Could not override silent mode while the phone is in Dnd mode");
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                // Restore settings to before the notification
                try {
                    audioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION, prevNotificationVolume, 0);
                    audioManager.setStreamVolume(AudioManager.STREAM_ALARM, prevAlarmVolume, 0);
                    audioManager.setRingerMode(prevRingerMode);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        notificationManager.setInterruptionFilter(finalPrevInterruptionFilter);
                    }
                } catch (SecurityException e) {
                    Log.e(TAG, "Could not restore ringer mode while the phone is in Dnd mode");
                }
            }
        }, length);
    }
}