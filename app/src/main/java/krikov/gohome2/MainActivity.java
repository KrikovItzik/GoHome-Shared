package krikov.gohome2;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.media.RingtoneManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends ActionBarActivity {
    public TimePicker pickerTime;
    public Button OnButton;
    String WorkTimeParameter[];
    String dbString;
    static final int UniqueID = 10101978;
    int Hrs;
    int Min;
    public String chosenRingtone;
    DBHandler dbHandler;
    private PendingIntent pendingIntent;
    private PendingIntent PreAlarm_PendIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pickerTime = (TimePicker) findViewById(R.id.timePicker);
        pickerTime.setIs24HourView(true);
        pickerTime.getCurrentHour();
        pickerTime.getCurrentMinute();
        dbHandler = new DBHandler(this, null, null,  1);
        //String[] separated = WorkTimeParameter.split(":");
        //int AddHour = Integer.valueOf(separated[1]);
        //int AddMin = Integer.valueOf(separated[2]);
        DBQuery("tbl_Teken", "teken", "");
        if (dbString.equals("") )
        {
            GetUserWorkTimer() ;
        }
        showtime();

    }

    public void showtime() {
        OnButton = (Button) findViewById(R.id.button);
        OnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExtraHours();
                DBQuery("tbl_Teken","teken","");
                WorkTimeParameter = dbString.split(":");
                Hrs = pickerTime.getCurrentHour() + Integer.valueOf(WorkTimeParameter[0]);
                Min = pickerTime.getCurrentMinute() + Integer.valueOf(WorkTimeParameter[1]);
                if (Min > 59) {
                    Hrs = Hrs + 1;
                    Min = Min - 60;
                }
                if (Hrs > 24) {
                    Hrs = Hrs - 24;
                }
                dbHandler.addData("tbl_Notification","notification",Hrs+":"+Min);
                Toast.makeText(getBaseContext(), "ללכת הביתה ב :" + Hrs + ":" + Min, Toast.LENGTH_LONG).show();
                alarmMethod();
            }
        });

    }

    public void alarmMethod() {
        Intent SetAlarm = new Intent(this, NotifyService.class);
        Intent PreAlarm_Intent = new Intent(this, NotifyServicePreAlarm.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        pendingIntent = PendingIntent.getService(this, 0, SetAlarm, 0);
        PreAlarm_PendIntent = PendingIntent.getService(this, 0,PreAlarm_Intent, 0);

        int currentDayOfMonth; // Define int for Current Day of the Month

        Calendar c = Calendar.getInstance(); // Define Calendar Object
//        int Year = c.get(Calendar.YEAR);
//        int Day = c.get(Calendar.DAY_OF_MONTH);
//        int Month = c.get(Calendar.AM_PM);
        int Hours = pickerTime.getCurrentHour();
        int Minutes = pickerTime.getCurrentMinute();
        int CalcHours = pickerTime.getCurrentHour() + Integer.valueOf(WorkTimeParameter[0]);
        int CalcMinutes = pickerTime.getCurrentMinute() + Integer.valueOf(WorkTimeParameter[1]);

        if (CalcHours >= 24 || CalcHours >=23 && CalcMinutes >= 60) {
            currentDayOfMonth = c.get(Calendar.DAY_OF_MONTH) + 1;
        }
        else {
            currentDayOfMonth = c.get(Calendar.DAY_OF_MONTH);
        }
        int currentMonth = c.get(Calendar.MONTH);
        int currentYear = c.get(Calendar.YEAR);
        int amorpm;
        if (Hrs > 12 ) {
            amorpm = 1;
            Hrs = Hrs - 12;}
        else {
            amorpm = 0;
        }
        int currentHour = Hrs;
        int currentMinutes = Min;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.SECOND, 5);
        calendar.set(Calendar.MINUTE, currentMinutes);
        calendar.set(Calendar.HOUR, currentHour);
        calendar.set(Calendar.AM_PM, amorpm);
        calendar.set(Calendar.DAY_OF_MONTH, currentDayOfMonth);
        calendar.set(Calendar.MONTH, currentMonth);
        calendar.set(Calendar.YEAR,currentYear );
        String PreAlarmFromDB = dbHandler.getDataFromDB("tbl_PRE_ALARM","pre_alarm","");
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Integer PrePre = currentMinutes - Integer.valueOf(PreAlarmFromDB);
        calendar.set(Calendar.MINUTE, PrePre);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PreAlarm_PendIntent);
        calendar.clear();
        c.clear();
        Toast.makeText(MainActivity.this, "התראה תופיע בשעה שנקבעה", Toast.LENGTH_SHORT).show();
        //moveTaskToBack(true);
    }

    public void ExtraHours(){
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.extra_time,
                (ViewGroup) findViewById(R.id.layout_dialog));

        popDialog.setIcon(android.R.drawable.ic_menu_help);
        popDialog.setTitle("תכנון שעות נוספות להיום");
        popDialog.setView(Viewlayout);

        // Button OK
        popDialog.setPositiveButton("אישור",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }

                });

        popDialog.create();
        popDialog.show();

    }

    private void GetUserWorkTimer() {
        final EditText textInput = new EditText(this);
        DBQuery("tbl_Teken","teken","");
        textInput.setText(dbString);
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("אנא הכנס שעות תקן");
        alertDialog.setIcon(R.mipmap.ic_launcher);
        alertDialog.setView(textInput);
        alertDialog.setPositiveButton("אישור",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String userSet = textInput.getText().toString();
                String tbl_Name = "tbl_Teken";
                String tbl_Column = "teken";
                String tbl_Data = userSet.toString() ;
                dbHandler.addData(tbl_Name,tbl_Column,tbl_Data);
                Toast.makeText(getBaseContext(),"שעות תקן נשמרו",Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("ביטול",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getBaseContext(), "לא נשמר", Toast.LENGTH_SHORT).show();
            }

        });
        AlertDialog saveData = alertDialog.create();
        saveData.show();

    }

    public void DBQuery(String tbl_Name,String tbl_Column,String tbl_Data){
        dbString = dbHandler.getDataFromDB(tbl_Name,tbl_Column ,tbl_Data);
        //Toast.makeText(getBaseContext(),dbString,Toast.LENGTH_SHORT).show();
    }

    private void showAbout(){
        final AlertDialog.Builder aleretDialog;
        aleretDialog = new AlertDialog.Builder(this);
        aleretDialog.setTitle("אודות");
        aleretDialog.setMessage("פותח על ידי איציק קריקוב 4/2015");
        aleretDialog.setIcon(R.drawable.photo);
        aleretDialog.setNeutralButton("סגור",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        aleretDialog.show();

    }

    public void RemindMeinSlider(){
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.popup_slider,
                (ViewGroup) findViewById(R.id.layout_dialog));

        final TextView item1 = (TextView)Viewlayout.findViewById(R.id.txtItem1); // txtItem1

        popDialog.setIcon(android.R.drawable.ic_lock_idle_alarm);
        popDialog.setTitle("בחר התראה מוקדמת בין 0-30 דקות");
        popDialog.setView(Viewlayout);

        //  seekBar1
        final SeekBar seek1 = (SeekBar) Viewlayout.findViewById(R.id.seekBar1);
        seek1.setMax(30);
        dbString = null;
        DBQuery("tbl_PRE_ALARM","pre_alarm","");
        if (dbString == "") {
            seek1.setProgress(15);
        }
        else {
            seek1.setProgress(Integer.valueOf(dbString));
        }
        item1.setText("דקות : "+seek1.getProgress());
        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                //Do something here with new value
                item1.setText("דקות : " + progress);
            }

            public void onStartTrackingTouch(SeekBar arg0) {
                // TODO Auto-generated method stub

            }

            public void onStopTrackingTouch(SeekBar seekBar) {
                // TODO Auto-generated method stub

            }
        });


        // Button OK
        popDialog.setPositiveButton("אישור",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Integer UserProgress = seek1.getProgress();
                        dbHandler.addData("tbl_PRE_ALARM","pre_alarm",String.valueOf(UserProgress));
                        dialog.dismiss();
                        Toast.makeText(getBaseContext(),"התראה ראשונה "+seek1.getProgress()+" דקות לפני סיום שעות התקן ",Toast.LENGTH_SHORT).show();
                    }

                });
        popDialog.create();
        popDialog.show();

    }

    private void NotificationSoundSelect() {
        Intent RingTonePick = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        RingTonePick.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        RingTonePick.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "בחר צליל התראה");
        RingTonePick.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(RingTonePick, 5);
    }

    public void Pre_Alarm(){
        //Intent myIntent = new Intent(this, NotifyService.class);






    }

    @Override
    protected void onActivityResult(final int requestCode, final int resultCode, final Intent RingTonePick)
    {
        if (resultCode == Activity.RESULT_OK && requestCode == 5)
        {
            Uri uri;
            uri = RingTonePick.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);

            if (uri != null)
            {
                this.chosenRingtone = uri.toString();
                dbHandler.addData("tbl_Configuration","SelectedRingtone",uri.toString());
                Toast.makeText(getBaseContext(), "צליל התראה נשמר", Toast.LENGTH_SHORT).show();
            }
            else
            {
                this.chosenRingtone = null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.tekentime:
                GetUserWorkTimer();
                return true;
            case R.id.action_about:
                showAbout();
                return true;
            case R.id.NotificationSound:
                NotificationSoundSelect();
                return true;
            case R.id.DeleteAllDB:
                dbHandler.deleteTable("tbl_Teken");
                dbHandler.deleteTable("tbl_Notification");
                Toast.makeText(getBaseContext(), "כל המידע נמחק במסד הנתונים", Toast.LENGTH_SHORT).show();
                GetUserWorkTimer();
                return true;
            case R.id.ShowWhenAlert:
                DBQuery("tbl_Notification", "notification", "");
                if (dbString.equals(""))
                {
                    Toast.makeText(getBaseContext(), "לא נמצאו התראות", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), "התראה נקבעה לשעה:" + dbString, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.DBreCreate:
                dbHandler.dropAllTable();
                return true;
            case R.id.RemindMeIn:
                RemindMeinSlider();
                return true;

        }

        return super.onOptionsItemSelected(item);
        }
}
