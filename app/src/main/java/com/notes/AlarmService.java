package com.notes;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import com.notes.ui.turn_off_alarm.TurnOffAlarmActivity;

public class AlarmService extends Service {

    private Ringtone ringtone;
    private static final String ACTION_DISMISS = "STOP_SERVICE";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction() == ACTION_DISMISS)
            stopSelf();
        else {
            Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (alarmUri == null)
                alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(this, alarmUri);
            ringtone.play();
            createNotificationChannel();
            showNotification();
        }
        return START_NOT_STICKY;
    }

    private void showNotification() {
        Intent intent = new Intent(this, TurnOffAlarmActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("time", System.currentTimeMillis());
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 100, intent, PendingIntent.FLAG_IMMUTABLE);
        Intent stopService = new Intent(this, AlarmService.class);
        stopService.setAction(ACTION_DISMISS);
        PendingIntent stopServicePending = PendingIntent.getService(this, 123, stopService, 0);
        NotificationCompat.Builder notification = new NotificationCompat.Builder(this, "1")
                .setSmallIcon(R.drawable.ic_baseline_picture_as_pdf_24)
                .setContentTitle("Alarm")
                .setContentText("Cohu pi gjumi")
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setContentIntent(pendingIntent)
                .setFullScreenIntent(pendingIntent, true)
                .setAutoCancel(true)
                .addAction(R.drawable.ic_baseline_date_range_24, "Stop", stopServicePending);
        //NotificationManagerCompat.from(this).notify(1, notification.build());
        startForeground(1, notification.build());
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Channel25";
            String description = "Description";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("1", name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public void onDestroy() {
        if (ringtone != null)
            ringtone.stop();
        super.onDestroy();
    }
}
