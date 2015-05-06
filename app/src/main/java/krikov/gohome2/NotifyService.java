package krikov.gohome2;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

public class NotifyService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        DBHandler dbHandler;
        dbHandler = new DBHandler(this, null, null,  1);
        String SoundAlarm = dbHandler.getDataFromDB("tbl_Configuration","SelectedRingtone","");
        NotificationManager manager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setAutoCancel(true);
        builder.setContentTitle("זמן ללכת הביתה");
        builder.setContentText("סיימת את שעות התקן להיום");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setDefaults(Notification.DEFAULT_VIBRATE);
        builder.setSound(Uri.parse(SoundAlarm));
        Notification notification = builder.build();
        //int notID = (int) ((Math.random() * 10)+1);
        manager.notify(MainActivity.UniqueID, notification);
        dbHandler.deleteTable("tbl_Notification");
        stopSelf();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        stopSelf();
    }

        //Notification mNotify = new Notification.Builder(this)
        //        .setContentTitle("זמן ללכת הביתה")
        //        .setContentText("סיימת את שעות התקן להיום")
        //        .setSmallIcon(R.drawable.ic_launcher)
        //        .setContentIntent(pIntent)
        //        .setSound(sound)
        //.addAction(0, "Load Website", pIntent)
        //        .build();

        //mNM.notify(1, mNotify);
    }



