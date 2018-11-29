package martin.derek.mototracker;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        Toast.makeText(context, "MAKING ALARM", Toast.LENGTH_SHORT).show();
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");
                LocalData localData = new LocalData(context);
                NotificationScheduler.setReminder(context, AlarmReceiver.class, localData.get_year(), localData.get_month(),localData.get_day(), intent.getStringExtra("Body"));
                return;
            }
        }



        Log.d(TAG, "onReceive: ");
        Toast.makeText(context,         intent.getExtras().keySet().iterator().next() +" : "+intent.getExtras().size() , Toast.LENGTH_SHORT).show();
        //Trigger the notification
        NotificationScheduler.showNotification(context, MainActivity.class,
                "Reminder", intent.getStringExtra("Body"));

    }
}