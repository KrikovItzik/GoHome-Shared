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
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {
    private RadioGroup RG;
    private TimePicker TP;
    public TimePicker pickerTime;
    public Button OnButton;
    String WorkTimeParameter[];
    String dbString;
    static final int UniqueID = 10101978;
    public String time;
    int Hrs;
    int Min;
    public int CalcHours;
    public int CalcMinutes;
    public String chosenRingtone;
    DBHandler dbHandler;
    public PendingIntent pendingIntent;
    public PendingIntent PreAlarm_PendIntent;
    public String extTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pickerTime = (TimePicker) findViewById(R.id.timePicker);
        pickerTime.setIs24HourView(true);
        pickerTime.getCurrentHour();
        pickerTime.getCurrentMinute();
        dbHandler = new DBHandler(this, null, null,  1);
        UpdateTvNote();
        DBQuery("tbl_Teken", "teken", "");
        if (dbString.equals("") )
        {
            GetUserWorkTimer() ;
        }
        DBQuery("tbl_ExtraTime", "AllowExtraTime", "");
        if (dbString.equals(""))
        {
            extTime = "";
        }
        else
        {
            extTime = "Yes";
        }
        showtime();
    }

    public void GetSetttingsForExtraTime(){
        if (extTime.equals("Yes") )
        {
            ExtraHours();
        }
        else
        {
            SetAlarm(0);
        }
    }

    public void SetAlarm(Integer idx){
        DBQuery("tbl_Teken", "teken", "");
        WorkTimeParameter = dbString.split(":");

        if (idx == 0 ) {
            Hrs = pickerTime.getCurrentHour() + Integer.valueOf(WorkTimeParameter[0]);
            Min = pickerTime.getCurrentMinute() + Integer.valueOf(WorkTimeParameter[1]);
            CalcHours = pickerTime.getCurrentHour() + Integer.valueOf(WorkTimeParameter[0]);
            CalcMinutes =  pickerTime.getCurrentMinute() + Integer.valueOf(WorkTimeParameter[1]) ;
        }
        if (idx == 1){
            Hrs = pickerTime.getCurrentHour() + Integer.valueOf(WorkTimeParameter[0]) + 3;
            Min = pickerTime.getCurrentMinute() + Integer.valueOf(WorkTimeParameter[1]) + 30;
            CalcHours = pickerTime.getCurrentHour() + Integer.valueOf(WorkTimeParameter[0]) + 3;
            CalcMinutes =  pickerTime.getCurrentMinute() + Integer.valueOf(WorkTimeParameter[1]) + 30 ;
        }
        if (idx == 2){
            Hrs = pickerTime.getCurrentHour() + Integer.valueOf(WorkTimeParameter[0]) + 6;
            Min = pickerTime.getCurrentMinute() + Integer.valueOf(WorkTimeParameter[1]);
            CalcHours = pickerTime.getCurrentHour() + Integer.valueOf(WorkTimeParameter[0]) + 6;
            CalcMinutes =  pickerTime.getCurrentMinute() + Integer.valueOf(WorkTimeParameter[1]);
        }
        if (Min > 59) {
            Hrs = Hrs + 1;
            Min = Min - 60;
        }
        if (Hrs >= 24) {
            Hrs = Hrs - 24;
        }
        if (Min == 0) {
            time = Hrs + ":00";
        }
        else
        {
            if (Min < 10){
                time = Hrs+":0"+Min;
            }
            else
            {
                time = Hrs + ":" + Min;
            }
        }
        dbHandler.addData("tbl_Notification", "notification", time);
        UpdateTvNote();
//        Toast.makeText(getBaseContext(), "ללכת הביתה ב "+ time, Toast.LENGTH_LONG).show();
        alarmMethod();
    }

    public void UpdateTvNote(){
        DBQuery("tbl_Notification", "notification", "");
        if (dbString.equals(""))
        {
            TextView text_tv_Note = (TextView) findViewById(R.id.tv_Note);
            text_tv_Note.setText(R.string.alert_not_found);
        }
        else
        {
            time = dbString;
            TextView text_tv_Note = (TextView) findViewById(R.id.tv_Note);
            text_tv_Note.setText(getString(R.string.time_finished_teken) + " " + time);
        }
    }

    public void showtime() {
        OnButton = (Button) findViewById(R.id.button);
        OnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GetSetttingsForExtraTime();

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
        Calendar calendar = Calendar.getInstance(); // Define Calendar Object

        if (CalcHours >= 24 || CalcHours >=23 && CalcMinutes >= 60)
        {
            currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH) + 1;
        }
        else
        {
            currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        }
        int currentMonth = calendar.get(Calendar.MONTH);
        int currentYear = calendar.get(Calendar.YEAR);
        int amorpm;
        if (Hrs > 12 ) {
            amorpm = 1;
            Hrs = Hrs - 12;}
        else {
            amorpm = 0;
        }
        int currentHour = Hrs;
        int currentMinutes = Min;
        calendar.set(Calendar.SECOND, 5);
        calendar.set(Calendar.MINUTE, currentMinutes);
        calendar.set(Calendar.HOUR, currentHour);
        calendar.set(Calendar.AM_PM, amorpm);
        calendar.set(Calendar.DAY_OF_MONTH, currentDayOfMonth);
        calendar.set(Calendar.MONTH, currentMonth);
        calendar.set(Calendar.YEAR, currentYear);
        String PreAlarmFromDB = dbHandler.getDataFromDB("tbl_PRE_ALARM","pre_alarm","");
        if (PreAlarmFromDB.equals(""))
        {
            PreAlarmFromDB = "15";
            dbHandler.addData("tbl_PRE_ALARM","pre_alarm","15");
        }
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Integer PrePre = currentMinutes - Integer.valueOf(PreAlarmFromDB);
        calendar.set(Calendar.MINUTE, PrePre);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), PreAlarm_PendIntent);
        calendar.clear();
        Toast.makeText(MainActivity.this, getString(R.string.alarm_will_show_in), Toast.LENGTH_SHORT).show();
        //moveTaskToBack(true);
    }

    private void GetUserWorkTimer() {
//        final AlertDialog.Builder popDialog1 = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.get_user_work_time_layout,
                (ViewGroup) findViewById(R.id.get_user_time_layout));

        DBQuery("tbl_Teken", "teken", "");
        AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.set_time_teken));
        //alertDialog.setIcon(R.drawable.);
        TP = (TimePicker) Viewlayout.findViewById(R.id.TP_Teken);
        TP.setIs24HourView(true);
        if (dbString.equals("")) {
            TP.setCurrentHour(9);
            TP.setCurrentMinute(0);
        }
        else
        {
            WorkTimeParameter =  dbString.split(":");
            TP.setCurrentHour(Integer.valueOf(WorkTimeParameter[0]));
            TP.setCurrentMinute(Integer.valueOf(WorkTimeParameter[1]));
        }
        alertDialog.setView(Viewlayout);
        alertDialog.setPositiveButton("אישור",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                String userSet = TP.getCurrentHour() + ":" + TP.getCurrentMinute();
                dbHandler.addData("tbl_Teken","teken",userSet);
                Toast.makeText(getBaseContext(),getString(R.string.data_saved),Toast.LENGTH_SHORT).show();
            }
        });
        alertDialog.setNegativeButton("ביטול",new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Toast.makeText(getBaseContext(), getString(R.string.data_not_saved), Toast.LENGTH_SHORT).show();
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
        final AlertDialog.Builder alertDialog;
        alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle(getString(R.string.about));
        alertDialog.setMessage("פותח על ידי איציק קריקוב 4/2015" );
        alertDialog.setIcon(R.drawable.photo);
        alertDialog.setNeutralButton(getString(R.string.Close), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        alertDialog.show();

    }

    public void ExtraHours(){
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.extra_time,
                (ViewGroup) findViewById(R.id.layout_extraTime));

        popDialog.setIcon(android.R.drawable.ic_menu_help);
        popDialog.setTitle(getString(R.string.Prepare_extra_hours));
        popDialog.setView(Viewlayout);
        RG = (RadioGroup) Viewlayout.findViewById(R.id.radioGroup);
        // Button OK
        popDialog.setPositiveButton(getString(R.string.Confirm),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                int radioButtonID = RG.getCheckedRadioButtonId();
                View radioButton = RG.findViewById(radioButtonID);
                int idx = RG.indexOfChild(radioButton);
                SetAlarm(idx);
                dialog.dismiss();
            }

        });

        popDialog.create();
        popDialog.show();

    }

    public void RemindMeinSlider(){
        final AlertDialog.Builder popDialog = new AlertDialog.Builder(this);
        final LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        final View Viewlayout = inflater.inflate(R.layout.popup_slider,
                (ViewGroup) findViewById(R.id.layout_dialog));

        final TextView item1 = (TextView)Viewlayout.findViewById(R.id.txtItem1); // txtItem1

        popDialog.setIcon(android.R.drawable.ic_lock_idle_alarm);
        popDialog.setTitle(getString(R.string.Select_Pre_Alarm));
        popDialog.setView(Viewlayout);

        //  seekBar1
        final SeekBar seek1 = (SeekBar) Viewlayout.findViewById(R.id.seekBar1);
        seek1.setMax(30);
        dbString = null;
        DBQuery("tbl_PRE_ALARM","pre_alarm","");
        if (dbString.equals("")) {
            seek1.setProgress(15);
        }
        else {
            seek1.setProgress(Integer.valueOf(dbString));
        }
        item1.setText(getString(R.string.Minutes)+": "+seek1.getProgress());
        seek1.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser){
                //Do something here with new value
                item1.setText(getString(R.string.Minutes)+": "+ progress);
            }

            public void onStartTrackingTouch(SeekBar arg0) {


            }

            public void onStopTrackingTouch(SeekBar seekBar) {


            }
        });


        // Button OK
        popDialog.setPositiveButton(getString(R.string.Confirm),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Integer UserProgress = seek1.getProgress();
                        dbHandler.addData("tbl_PRE_ALARM","pre_alarm",String.valueOf(UserProgress));
                        dialog.dismiss();
                        Toast.makeText(getBaseContext(),getString(R.string.pre_alarm)+" "+seek1.getProgress()+" "+getString(R.string.minutes_before_alarm),Toast.LENGTH_SHORT).show();
                    }

                });
        popDialog.create();
        popDialog.show();

    }

    private void NotificationSoundSelect() {
        Intent RingTonePick = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
        RingTonePick.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
        RingTonePick.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, getString(R.string.select_alarm_ringtone));
        RingTonePick.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, (Uri) null);
        this.startActivityForResult(RingTonePick, 5);
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
                Toast.makeText(getBaseContext(), getString(R.string.alarm_ringtone_saved), Toast.LENGTH_SHORT).show();
            }
            else
            {
                this.chosenRingtone = null;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        // Inflate the menu; this adds items to the action bar if it is present.
        if (extTime.equals(""))
        {
            MenuItem EW = menu.findItem(R.id.ExtraTime);
            EW.setChecked(false);
        }
        else
        {
            MenuItem EW = menu.findItem(R.id.ExtraTime);
            EW.setChecked(true);
        }
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
                dbHandler.dropAllTable();
                TextView text_tv_Note = (TextView) findViewById(R.id.tv_Note);
                text_tv_Note.setText(getString(R.string.alert_not_found));
                Toast.makeText(getBaseContext(), getString(R.string.all_personal_information_deleted), Toast.LENGTH_SHORT).show();
                GetUserWorkTimer();
                return true;
            case R.id.ShowWhenAlert:
                DBQuery("tbl_Notification", "notification", "");
                if (dbString.equals(""))
                {
                    Toast.makeText(getBaseContext(), getString(R.string.alert_not_found), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getBaseContext(), getString(R.string.alarm_was_set_to)+ dbString, Toast.LENGTH_SHORT).show();
                }
                return true;
            case R.id.DBreCreate:
                text_tv_Note = (TextView) findViewById(R.id.tv_Note);
                text_tv_Note.setText(getString(R.string.alert_not_found));
                dbHandler.dropAllTable();
                return true;
            case R.id.RemindMeIn:
                RemindMeinSlider();
                return true;
            case R.id.ExtraTime:
                if (item.isChecked()) {
                    dbHandler.addData("tbl_ExtraTime","AllowExtraTime","");
                    item.setChecked(false);
                    extTime = "";
                }
                else
                {
                    dbHandler.addData("tbl_ExtraTime","AllowExtraTime","Yes");
                    item.setChecked(true);
                    extTime = "Yes";
                }

                return  true;


        }

        return super.onOptionsItemSelected(item);
    }
}
