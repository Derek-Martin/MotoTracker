package martin.derek.mototracker;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;


public class NotificationFragment extends android.support.v4.app.Fragment {

    private static final String TAG = "Notification";
    private OnFragmentInteractionListener mListener;
    private View MyView;
    private LocalData localData;
    private EditText editText;

    public NotificationFragment() {
        // Required empty public constructor
    }
    public void Setup(){
        localData = new LocalData(getContext());
        editText = MyView.findViewById(R.id.NotiEditText);
        ((Button)MyView.findViewById(R.id.NotiButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimePickerDialog(localData.get_year(),localData.get_month(),localData.get_day());
            }
        });




    }
    private void showTimePickerDialog(int y, int m,int d) {

        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.timepicker_header, null);
        DatePickerDialog builder = new DatePickerDialog(MyView.getContext(),R.style.DialogTheme,

                new DatePickerDialog.OnDateSetListener(){

                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        localData.set_day(i2);
                        localData.set_month(i1);
                        localData.set_year(i);
                        NotificationScheduler.setReminder(MainActivity.This,AlarmReceiver.class,i,i1,i2,editText.getText().toString());

                    }
                }, y, m,d);

        builder.setCustomTitle(view);
        builder.show();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        MyView = inflater.inflate(R.layout.fragment_notification, container, false);
        Setup();
        return MyView;

    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
