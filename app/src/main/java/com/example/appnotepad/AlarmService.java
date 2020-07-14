package com.example.appnotepad;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;


public class AlarmService extends Service {
    private ArrayList<String> dateTime;
    private ArrayList<String> title;
    private ArrayList<String> content;
    private String getTitle = "My title";
    private String getContent = "My content";
    private Ringtone ringtone;
    private Timer t = new Timer();
    public static final String CHANNEL_1_ID = "channel1";
    public static final String CHANNEL_2_ID = "channel2";
    private static final String CHANNEL_ID = "MyNotificationChannelI";
    private NotificationManagerCompat notificationManager;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        notificationManager = NotificationManagerCompat.from(this);
        dateTime = (ArrayList<String>) intent.getSerializableExtra("datetime");
        title = (ArrayList<String>) intent.getSerializableExtra("title");
        content = (ArrayList<String>) intent.getSerializableExtra("content");
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));

        t.scheduleAtFixedRate(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                for (int i = 0; i < dateTime.size(); i++) {
                    Log.i("main",GetDateTime());
                    Log.i("time",dateTime.get(i));
                    if (GetDateTime().equals(dateTime.get(i))) {
                        getTitle = title.get(i);
                        getContent = content.get(i);
                        ringtone.play();
                        startMyOwnForeground();
                        ringtone.stop();
                    } else {
                        ringtone.stop();
                    }
                }
            }
        }, 0, 1000);

        return super.onStartCommand(intent, flags, startId);

    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void startMyOwnForeground() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        String NOTIFICATION_CHANNEL_ID = "com.example.appNote";
        String channelName = "My Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle(getTitle)
                .setContentText(getContent)
                .setSmallIcon(R.drawable.ic_notepad)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(1, notification);
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

    public int CheckDate(ArrayList<String> date) {
        int check = 0;
        for (int i = 0; i < date.size(); i++) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
            try {
                Date date1 = sdf.parse(GetDateTime());
                Date date2 = sdf.parse(date.get(i));
                if (date1.before(date2)) {
                    check = 1;
                    break;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }
        return check;
    }

}

