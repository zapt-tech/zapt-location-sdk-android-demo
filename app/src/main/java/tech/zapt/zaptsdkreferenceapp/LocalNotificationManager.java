package tech.zapt.zaptsdkreferenceapp;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.VibrationEffect;
import android.provider.Settings;

import androidx.annotation.Keep;

@Keep
public class LocalNotificationManager {

    public static final int FOREGROUND_SERVICE_NOTIFICATION_ID = 1;
    public static final int DEFAULT_NOTIFICATION_ID = 2;
    public String NOTIFICATION_CHANNEL_ID;
    public String NOTIFICATION_NAME;
    public String NOTIFICATION_CHANNEL_DESCRIPTION;

    private boolean vibrateEnabled;

    private boolean soundEnabled;

    private int icon;

    private Context context;

    private SharedPreferences preferences;

    private static LocalNotificationManager INSTANCE;

    private String defaultChannelId;

    private String soundOnlyChannelId;

    private String vibrateOnlyChannelId;

    private int vibrateDuration = 1500;

    private Class<? extends Activity> activity;

    private LocalNotificationManager(Context context){
        this.context = context;
        NOTIFICATION_CHANNEL_ID = getDefaultNotificationChannelId();
        NOTIFICATION_NAME = getNotificationName();
        NOTIFICATION_CHANNEL_DESCRIPTION = getNotificationChannelDescription();

        preferences = context.getSharedPreferences("LocalNotificationManager", Context.MODE_PRIVATE);
        vibrateEnabled = preferences.getBoolean("vibrateEnabled", true);
        soundEnabled = preferences.getBoolean("soundEnabled", true);
        icon = R.drawable.ic_stat_name;
        this.createNotificationChannels();
    }

    public static LocalNotificationManager getInstance(Context context){
        if(INSTANCE == null) {
            INSTANCE = new LocalNotificationManager(context);
        }
        return INSTANCE;
    }

    protected void createNotificationChannels(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.createDefaultNotificationChannel();
            this.createOnlySoundNotificationChannel();
            this.createOnlyVibrateNotificationChannel();
        }
    }

    public void sendNotification(Class<? extends  Activity> activity, String message) {
        if(activity == null) {
            activity = this.activity;
        }

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(icon);
        if(message == null) {
            message = "Contato identificado. Afaste-se por seguranÃ§a.";
        }
        builder.setContentTitle(message);
        Intent intent = new Intent(context, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_IMMUTABLE
        );
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendNotificationForAndroid8AndAbove(builder);
        } else {
            sendNotificationForAndroid7AndLower(builder, activity);
        }
    }

    public void dismissNotification() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(DEFAULT_NOTIFICATION_ID);
    }

    public Notification getForegroundNotification(Class<? extends  Activity> activity, String message){
        if(activity == null) {
            activity = this.activity;
        }

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(icon);

        if(message == null) {
            message = "ProteÃ§Ã£o Ativada";
        }

        builder.setContentTitle(message);
        Intent intent = new Intent(context, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(pendingIntent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            builder.setChannelId(this.getDefaultNotificationChannelId());
        }
        return builder.build();
    }

    public void sendTransmitterErrorNotification(Class<? extends  Activity> activity, int code, String message) {
        if(activity == null) {
            activity = this.activity;
        }

        Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(icon);
        if(message != null) {
            builder.setContentTitle(message);
        } else {
            builder.setContentTitle("Erro ao transmitir sinal. CÃ³digo: " + code);
        }
        Intent intent = new Intent(context, activity);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT
        );
        builder.setContentIntent(pendingIntent);
        builder.setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            sendNotificationForAndroid8AndAbove(builder);
        } else {
            sendNotificationForAndroid7AndLower(builder, activity);
        }
    }


    @TargetApi(26)
    private void createDefaultNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_NAME, NotificationManager.IMPORTANCE_DEFAULT);

        channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build());
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{ 0 });

        notificationManager.createNotificationChannel(channel);

        defaultChannelId = channel.getId();
    }

    @TargetApi(26)
    private void createOnlySoundNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(getSoundNotificationChannelId(),
                getSoundNotificationChannelId(), NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(Settings.System.DEFAULT_NOTIFICATION_URI, new AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION)
                .build());
        channel.setVibrationPattern(new long[]{ 0 });
        channel.enableVibration(true);
        notificationManager.createNotificationChannel(channel);
        soundOnlyChannelId = channel.getId();
    }

    @TargetApi(26)
    private void createOnlyVibrateNotificationChannel() {
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        NotificationChannel channel = new NotificationChannel(getVibrateNotificationChannelId(),
                getVibrateNotificationChannelId(), NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null, null);
        channel.enableVibration(false);
        channel.setVibrationPattern(new long[]{ 0 });
        notificationManager.createNotificationChannel(channel);
        vibrateOnlyChannelId = channel.getId();
    }

    private void sendNotificationForAndroid8AndAbove(Notification.Builder builder){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            if(isSoundEnabled() && isVibrateEnabled()) {
                this.createDefaultNotificationChannel();
                builder.setChannelId(this.defaultChannelId);
                VibrationEffect vibrationEffect = VibrationEffect.createOneShot(vibrateDuration, VibrationEffect.DEFAULT_AMPLITUDE);
            } else if(isSoundEnabled()) {
                this.createOnlySoundNotificationChannel();
                builder.setChannelId(this.soundOnlyChannelId);
            } else {
                this.createOnlyVibrateNotificationChannel();
                builder.setChannelId(this.vibrateOnlyChannelId);
                VibrationEffect vibrationEffect = VibrationEffect.createOneShot(vibrateDuration, VibrationEffect.DEFAULT_AMPLITUDE);
            }

            builder.setAutoCancel(true);

            notificationManager.notify(DEFAULT_NOTIFICATION_ID, builder.build());
        }
    }

    private void sendNotificationForAndroid7AndLower(Notification.Builder builder, Class<? extends Activity> activity) {
        if(activity == null) {
            activity = this.activity;
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);

        stackBuilder.addNextIntent(new Intent(context, activity));

        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        builder.setContentIntent(resultPendingIntent);
        builder.setAutoCancel(true);

        if(isVibrateEnabled()) {
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
            builder.setVibrate(new long[]{ 0 });
            builder.setSound(null);
        } else {
            builder.setVibrate(new long[]{ 0 });
        }

        if(isSoundEnabled()) {
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }

        notificationManager.notify(DEFAULT_NOTIFICATION_ID, builder.build());
    }

    private String getDefaultNotificationChannelId(){
        return "NOTIFICATION_CHANNEL_ID";
    }

    private String getVibrateNotificationChannelId(){
        return "NOTIFICATION_CHANNEL_VIBRATE_ID";
    }

    private String getSoundNotificationChannelId(){
        return "NOTIFICATION_CHANNEL_SOUND_ID";
    }

    private String getNotificationName(){
        return "NOTIFICATION_NAME";
    }

    private String getNotificationChannelDescription(){
        return "NOTIFICATION_CHANNEL_DESCRIPTION";
    }

    public boolean isVibrateEnabled() {
        return vibrateEnabled;
    }

    public void setVibrateEnabled(boolean vibrateEnabled) {
        this.vibrateEnabled = vibrateEnabled;
        preferences.edit().putBoolean("vibrateEnabled", vibrateEnabled).commit();
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void setSoundEnabled(boolean soundEnabled) {
        this.soundEnabled = soundEnabled;
        preferences.edit().putBoolean("soundEnabled", soundEnabled).commit();
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public int getVibrateDuration() {
        return vibrateDuration;
    }

    public void setVibrateDuration(int vibrateDuration) {
        this.vibrateDuration = vibrateDuration;
    }

    public Class<? extends Activity> getActivity() {
        return activity;
    }

    public void setActivity(Class<? extends Activity> activity) {
        this.activity = activity;
    }
}
