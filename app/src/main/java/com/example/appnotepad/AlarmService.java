package com.example.appnotepad;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class AlarmService extends Service {
    private ArrayList<String> alarm;
    private Ringtone ringtone;
    private Timer t = new Timer();

    private static final String CHANNEL_ID = "MyNotificationChannelI";


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        alarm = (ArrayList<String>) intent.getSerializableExtra("alarm");

        ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        try {
            Intent notificationIntent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("My Alarm clock")
                    .setContentText("")
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build();

            startForeground(1, notification);

            NotificationChannel notificationChannel = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = new NotificationChannel(CHANNEL_ID, "My Alarm clock Service", NotificationManager.IMPORTANCE_DEFAULT);
            }
            NotificationManager notificationManager = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                notificationManager = getSystemService(NotificationManager.class);
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationManager.createNotificationChannel(notificationChannel);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


        t.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                for (int i = 0; i < alarm.size(); i++) {
                    if (GetDateTime().equals(alarm.get(i))) {
                        ringtone.play();
                    } else {
                        ringtone.stop();
                    }
                }
            }
        }, 0, 2000);

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        ringtone.stop();
        t.cancel();
        super.onDestroy();
    }

    public String GetDateTime() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            LocalDateTime now = LocalDateTime.now();
            return dtf.format(now);
        } else return null;
    }

}

