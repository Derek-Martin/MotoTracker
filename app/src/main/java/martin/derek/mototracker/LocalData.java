package martin.derek.mototracker;

import android.content.Context;
import android.content.SharedPreferences;


public class LocalData {

    private static final String APP_SHARED_PREFS = "RemindMePref";

    private SharedPreferences appSharedPrefs;
    private SharedPreferences.Editor prefsEditor;

    private static final String reminderStatus="reminderStatus";


    public LocalData(Context context)
    {
        this.appSharedPrefs = context.getSharedPreferences(APP_SHARED_PREFS, Context.MODE_PRIVATE);
        this.prefsEditor = appSharedPrefs.edit();
    }

    // Settings Page Set Reminder

    public boolean getReminderStatus()
    {
        return appSharedPrefs.getBoolean(reminderStatus, false);
    }

    public void setReminderStatus(boolean status)
    {
        prefsEditor.putBoolean(reminderStatus, status);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Hour)

    public int get_day()
    {
        return appSharedPrefs.getInt("day", 20);
    }

    public void set_day(int d)
    {
        prefsEditor.putInt("day", d);
        prefsEditor.commit();
    }

    // Settings Page Reminder Time (Minutes)

    public int get_month()
    {
        return appSharedPrefs.getInt("month", 0);
    }

    public void set_month(int m)
    {
        prefsEditor.putInt("month", m);
        prefsEditor.commit();
    }

    public int get_year()
    {
        return appSharedPrefs.getInt("year", 0);
    }

    public void set_year(int y)
    {
        prefsEditor.putInt("year", y);
        prefsEditor.commit();
    }

    public void reset()
    {
        prefsEditor.clear();
        prefsEditor.commit();

    }

}
