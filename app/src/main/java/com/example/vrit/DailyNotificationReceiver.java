package com.example.vrit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static androidx.core.content.ContextCompat.getSystemService;

public class DailyNotificationReceiver extends BroadcastReceiver {

    public static final String CHANNEL_ID = "VRIT_NOTIFICATIONS_CHANNEL_ID";
    public static final int NOTIFICATION_ID = 2297;

    @Override
    public void onReceive(Context context, Intent intent) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name =  "AttendanceNotification";
            String description = "Sent whenever the attendance is below 60%";
            int importance  = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(context , NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
        int requestID = (int) System.currentTimeMillis();

        Intent dailyPeriodAttendance = new Intent(context,DailyPeriodAttendance.class);
        dailyPeriodAttendance.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(context,requestID,dailyPeriodAttendance,PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder( context, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setContentTitle("Hey There! How was your Day?")
                .setContentText("Please check the periods that you have attended today")
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build() );

    }

}
