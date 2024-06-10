package com.hilma.plugin;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

public class NotificationChannelManager {

    public static final String RAW_CHANNEL_ID = "important_channel_";
    public static final String RAW_CHANNEL_NAME = "Notification Channel: ";
    public static final String RAW_CHANNEL_DESCRIPTION = "Notifications - app";

    public static final String ONGOING_CHANNEL_ID = "override_silent_mode_channel";

    public static final String TAG = "Hilma_Push_Notifications_Channel_Manager";

    public static final int ONGOING_NOTIFICATION_ID = 575;

    public static String getNotificationChannelId(String ringtone) {
        return RAW_CHANNEL_ID + ringtone;
    }

    public static String getNotificationChannelName(String ringtone) {
        return RAW_CHANNEL_NAME + ringtone;
    }

    public static Uri ringtoneToUri(Context context, String ringtone) {
        String appPgName = context.getApplicationContext().getPackageName();
        int resId = context.getApplicationContext().getResources().getIdentifier(ringtone, "raw", appPgName);
        return Uri.parse("android.resource://" + appPgName + "/" + resId);
    }

    public static NotificationManager getNotificationManager(Context context) {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static int getRingtoneLength(Context context, String ringtone) {
        MediaPlayer mediaPlayer = MediaPlayer.create(context, ringtoneToUri(context, ringtone));
        int duration = mediaPlayer.getDuration();
        mediaPlayer.release();
        return duration + 500;
    }

    public static void createNotificationChannelIfDoesntExist(Context context, String ringtone) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return;

        NotificationSettings settings = new NotificationSettings(context.getApplicationContext());

        String channelId = getNotificationChannelId(ringtone);
        String channelName = getNotificationChannelName(ringtone);

        NotificationManager manager = getNotificationManager(context);
        NotificationChannel channel = manager.getNotificationChannel(channelId);

        //if the channel already exists - get out of the function
        if (channel != null) return;

        channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);

        Uri sound = ringtoneToUri(context, ringtone);

        channel.setDescription(RAW_CHANNEL_DESCRIPTION);
        channel.setShowBadge(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            channel.setAllowBubbles(true);
        }

        channel.setBypassDnd(true);
        channel.setImportance(NotificationManager.IMPORTANCE_HIGH);
        channel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            channel.setBlockable(false);
        }

        //setting lights and vibration
        int[] lights = settings.getLights();
        long[] vibration = settings.getVibrationPattern();

        if (settings.isValidLights(lights)) {
            channel.enableLights(true);
            channel.setLightColor(lights[0]);
        }

        if (vibration.length > 0) {
            channel.enableVibration(true);
            channel.setVibrationPattern(vibration);
        }

        //Create audio attributes for the sound of the channel
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .build();

        channel.setSound(sound, audioAttributes);

        // Get the notification manager system service
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // Register the notification channel with the system
        notificationManager.createNotificationChannel(channel);
    }


    @SuppressLint("ForegroundServiceType")
    public static void deployOngoingNotification(Service service) {
        //Build the required background notification
        if (Build.VERSION.SDK_INT >= 26) {
            createOnGoingNotificationChannel(service);

            Notification notification = createOngoingNotification(service);

            service.startForeground(ONGOING_NOTIFICATION_ID, notification);
        }
    }

    public static int getNotificationIcon(Context context) {
        //getting the notification icon from the manifest
        Context appCtx = context.getApplicationContext();
        ApplicationInfo appInfo;
        try {
            appInfo = appCtx.getPackageManager().getApplicationInfo(appCtx.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            throw new RuntimeException(e);
        }

        return appInfo.metaData.getInt("com.google.firebase.messaging.default_notification_icon");
    }

    private static void createOnGoingNotificationChannel(Service service) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager manager = getNotificationManager(service.getApplicationContext());
            NotificationChannel channel = manager.getNotificationChannel(ONGOING_CHANNEL_ID);

            if (channel != null) return;

            channel = new NotificationChannel(ONGOING_CHANNEL_ID,
                    "Background Notification Channel",
                    NotificationManager.IMPORTANCE_LOW);

            channel.setShowBadge(false);

            manager.createNotificationChannel(channel);
        }
    }

    private static Notification createOngoingNotification(Service service) {
        return new NotificationCompat.Builder(service, ONGOING_CHANNEL_ID)
                .setSmallIcon(getNotificationIcon(service.getApplicationContext()))
                .setContentTitle("Background notification manager active")
                .setContentText("Background notification manager active")
                .setPriority(NotificationCompat.PRIORITY_LOW)
                .setSilent(true)
                .setSound(null)
                .setVibrate(null)
                .build();
    }
}