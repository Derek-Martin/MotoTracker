package martin.derek.mototracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import java.util.Set;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "MAKING ALARM", Toast.LENGTH_SHORT).show();
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");
                Toast.makeText(context, "BOOT COMPLETED", Toast.LENGTH_SHORT).show();

//                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_year(), localData.get_month(),localData.get_day(), intent.getStringExtra("Body"));
                return;
            }
        }

        SharedPreferences appSharedPrefs = context.getSharedPreferences("Notifications", Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = appSharedPrefs.edit();
        Log.d(TAG, "onReceive: ");

//        Set<String> Noti = appSharedPrefs.getStringSet("noti",null);


//        Toast.makeText(context,"SHOWING" , Toast.LENGTH_SHORT).show();
        //Trigger the notification
        NotificationScheduler.showNotification(context, MainActivity.class,"Maintenance Reminder", intent.getStringExtra("body"));




    }
}