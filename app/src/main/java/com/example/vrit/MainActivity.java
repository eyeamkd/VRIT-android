package com.example.vrit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

public class MainActivity extends AppCompatActivity {
    StudentDBClass database;
    public static final String CHANNEL_ID ="VRIT_NOTIFICATION";
    public static final int SPLASH_TIME_OUT =  4000;
    private PendingIntent dailyNotificationIntent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener( MainActivity.this,  new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                String mToken = instanceIdResult.getToken();
                Log.e("Token",mToken);
            }
        });
        sendWelcomeNotification();
        createNotificationChannel();
        scheduledNotifications();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d("TAG","in main activity");
                Intent intent = new Intent(MainActivity.this,Dashboard.class);
                startActivity(intent);
                finish();
            }
        },2500);

    }

    public void sendWelcomeNotification(){

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.common_google_signin_btn_icon_light)
                .setContentTitle("VRIT-Attendance")
                .setContentText("Your Attendance is below 60%")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setAutoCancel(true);

        int notificationId = 2297;

        NotificationManagerCompat notificationManagerCompat =  NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(notificationId,builder.build() );

    }


    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name =  "AttendanceNotification";
            String description = "Sent whenever the attendance is below 60%";
            int importance  = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);

        }
    }

    public void scheduledNotifications(){
        AlarmManager alarmManager = (AlarmManager)this.getSystemService(ALARM_SERVICE);
        Intent intent = new Intent(this,DailyNotificationReceiver.class );
        dailyNotificationIntent = PendingIntent.getBroadcast(this, 1, intent, 0);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP,
                System.currentTimeMillis()+10000,
                20000,
                dailyNotificationIntent);

        Log.d("SCHEDULED","in alarm manager function");
    }
}
