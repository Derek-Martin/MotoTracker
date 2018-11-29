package martin.derek.mototracker;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.widget.CardView;
import android.widget.Toast;

import java.time.Month;
import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by Jaison on 20/06/17.
 */

public class NotificationScheduler
{
    public static final int DAILY_REMINDER_REQUEST_CODE=100;
    public static final String TAG="NotificationScheduler";

    public static void setReminder(Context context,Class<?> cls,int year, int month,int day, String content)
    {
        Calendar setcalendar = Calendar.getInstance();
//        setcalendar.set(Calendar.DAY_OF_MONTH, day);
//        setcalendar.set(Calendar.MONTH, month);
//        setcalendar.set(Calendar.YEAR, year);
//        setcalendar.set(Calendar.HOUR_OF_DAY,4);
//        setcalendar.set(Calendar.MINUTE,46);
//        setcalendar.set(Calendar.SECOND,0);
        // cancel already scheduled reminders
//        cancelReminder(context,cls);
//
//        if(setcalendar.before(calendar))
//            setcalendar.add(Calendar.DATE,1);

        // Enable a receiver
        setcalendar.set(year,month,day,setcalendar.get(Calendar.HOUR_OF_DAY),setcalendar.get(Calendar.MINUTE)+1,setcalendar.get(Calendar.SECOND));
        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);


        Intent intent1 = new Intent(context, cls);
        intent1.putExtra("Body",content);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, setcalendar.get(Calendar.MILLISECOND), intent1, PendingIntent.FLAG_UPDATE_CURRENT|  Intent.FILL_IN_DATA);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.set(AlarmManager.RTC_WAKEUP, setcalendar.getTimeInMillis(), pendingIntent);

    }

    public static void cancelReminder(Context context,Class<?> cls)
    {
        // Disable a receiver

        ComponentName receiver = new ComponentName(context, cls);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

        Intent intent1 = new Intent(context, cls);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, DAILY_REMINDER_REQUEST_CODE, intent1, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager am = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        am.cancel(pendingIntent);
        pendingIntent.cancel();
    }

    public static void showNotification(Context context,Class<?> cls,String title,String content)
    {
//        Toast.makeText(context, "MAKING NOTIFACTIOPN", Toast.LENGTH_SHORT).show();
                NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT);


        CharSequence name = "Moto-Tracker";//getString(R.string.channel_name);
        String description = "Desc";//getString(R.string.channel_description);
        int importance = NotificationManager.IMPORTANCE_DEFAULT;
        NotificationChannel channel = new NotificationChannel("CHANNEL_ID", name, importance);
        // Register the channel with the system; you can't change the importance
        channel.setDescription(description);
        // or other notification behaviors after this
        NotificationManager notificationManager = MainActivity.This.getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);



// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1335, notificationBuilder.build());

//        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//
//        Intent notificationIntent = new Intent(context, cls);
//        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
//        stackBuilder.addParentStack(cls);
//        stackBuilder.addNextIntent(notificationIntent);
//
//        PendingIntent pendingIntent = stackBuilder.getPendingIntent(DAILY_REMINDER_REQUEST_CODE, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
//
//        Notification notification = builder.setContentTitle(title)
//                .setContentText(content)
//                .setAutoCancel(true)
//                .setSound(alarmSound)
//                .setSmallIcon(R.mipmap.ic_launcher_round)
//                .setContentIntent(pendingIntent).build();
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(DAILY_REMINDER_REQUEST_CODE, notification);

    }

}
