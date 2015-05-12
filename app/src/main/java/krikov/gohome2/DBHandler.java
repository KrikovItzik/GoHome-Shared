package krikov.gohome2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Switch;


public class DBHandler extends SQLiteOpenHelper {
    //Database Name and Version
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "GoHome.db";

    //Table Notification
    public static final String TABLE_TEKEN = "tbl_Teken";
    public static final String TABLE_TEKEN_COLUMN_ID = "_id";
    public static final String TABLE_TEKEN_COLUMN_TEKEN = "teken";

    //Notification Time Table
    public static final String TABLE_NOTIFICATION = "tbl_Notification";
    public static final String TABLE_NOTIFICATION_COLUMN_ID = "_id";
    public static final String TABLE_NOTIFICATION_NOTIFICATION = "notification";

    //Table Pre Alarm
    public static final String TABLE_PRE_ALARM = "tbl_PRE_ALARM";
    public static final String TABLE_PRE_ALARM_COLUMN_ID = "_id";
    public static final String TABLE_PRE_ALARM_COLUMN_Pre_Alarm = "pre_alarm";

    //Table Configuration
    public static final String TABLE_CONFIGURATION = "tbl_Configuration";
    public static final String CONFIGURATION_COLUMN_ID = "_id";
    public static final String CONFIGURATION_COLUMN_RINGTONE = "SelectedRingtone";

    //Table ExtraTime
    public static final String TABLE_EXTRATIME = "tbl_ExtraTime";
    public static final String EXTRATIME_COLUMN_ID = "_id";
    public static final String EXTRATIME_COLUMN_EXTRATIME = "AllowExtraTime";

    //We need to pass database information along to superclass
    public DBHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        String queryTeken = "CREATE TABLE " + TABLE_TEKEN + "(" +
                TABLE_TEKEN_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TABLE_TEKEN_COLUMN_TEKEN + " TEXT " +
                ");";
        db.execSQL(queryTeken);

        String queryNotification = "CREATE TABLE " + TABLE_NOTIFICATION + "(" +
                TABLE_NOTIFICATION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TABLE_NOTIFICATION_NOTIFICATION + " TIME " +
                ");";
        db.execSQL(queryNotification);

        String queryPreAlarm = "CREATE TABLE " + TABLE_PRE_ALARM + "(" +
                TABLE_PRE_ALARM_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TABLE_PRE_ALARM_COLUMN_Pre_Alarm + " TEXT " +
                ");";
        db.execSQL(queryPreAlarm);

        String queryConfiguration = "CREATE TABLE " + TABLE_CONFIGURATION + "(" +
                CONFIGURATION_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                CONFIGURATION_COLUMN_RINGTONE + " TEXT " +
                ");";
        db.execSQL(queryConfiguration);

        String queryExtraTime = "CREATE TABLE " + TABLE_EXTRATIME + "(" +
                EXTRATIME_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                EXTRATIME_COLUMN_EXTRATIME + " TEXT " +
                ");";
        db.execSQL(queryExtraTime);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEKEN);
        onCreate(db);
    }

    //Add a new row to the database
    public void addTeken(SetValues Teken){
        SQLiteDatabase db = getReadableDatabase();
        onDeleteDB("tbl_Teken");
        onCreate(db);
        ContentValues values = new ContentValues();
        values.put(TABLE_TEKEN_COLUMN_TEKEN, Teken.get_teken());
        db.insert(TABLE_TEKEN, null, values);
        db.close();
    }

    public void addData(String tbl_Name,String tbl_Column,String tbl_Data){
        SQLiteDatabase db = getReadableDatabase();
        deleteTable(tbl_Name);
        ContentValues values = new ContentValues();
        values.put(tbl_Column, tbl_Data);
        db.insert(tbl_Name, null, values);
        db.close();
    }

    // Delete Table
    public void  deleteTable(String tbl_Name){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tbl_Name);
    }

    // Drop Table
    public void dropAllTable(){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS tbl_Teken");
        db.execSQL("DROP TABLE IF EXISTS tbl_Notification");
        db.execSQL("DROP TABLE IF EXISTS tbl_Configuration");
        db.execSQL("DROP TABLE IF EXISTS tbl_PRE_ALARM");
        db.execSQL("DROP TABLE IF EXISTS tbl_ExtraTime");
        onCreate(db);
    }

    //Delete a product from the database
    public void deleteFromDB(String tbl_Name,String tbl_Column,String tbl_Data){
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("DELETE FROM " + tbl_Name + " WHERE " + tbl_Column + "=\"" + tbl_Data + "\";");
    }

    public String getDataFromDB(String tbl_Name,String tbl_Column ,String tbl_Data){
        String dbString = "";
        SQLiteDatabase db = getWritableDatabase();
        String query = "SELECT * FROM " + tbl_Name + " WHERE 1";

        //Cursor points to a location in your results
        Cursor c = db.rawQuery(query, null);
        //Move to the first row in your results
        c.moveToFirst();

        //Position after the last row means the end of the results
        while (!c.isAfterLast()) {
            if (c.getString(c.getColumnIndex(tbl_Column)) != null) {
                dbString += c.getString(c.getColumnIndex(tbl_Column));
                //dbString += "\n";
            }
            c.moveToNext();
        }
        db.close();
        return dbString;
    }

    public void onDeleteDB(String tbl_Name) {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + tbl_Name);
        onCreate(db);

    }
}
