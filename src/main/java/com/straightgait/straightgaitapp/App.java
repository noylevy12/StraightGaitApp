package com.straightgait.straightgaitapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.graphics.Color;
import android.os.Build;

public class App extends androidx.multidex.MultiDexApplication{
    public static final String CHANNEL_1_ID = "channel1";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationsChannels();

    }

    private void createNotificationsChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel1 = new NotificationChannel(
                    CHANNEL_1_ID,
                    "StraightGait App",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel1.enableVibration(true);
            channel1.setDescription("Straight you'r leg!");
            channel1.enableLights(true);
            channel1.setLightColor(Color.GREEN);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel1);
        }
    }
}
