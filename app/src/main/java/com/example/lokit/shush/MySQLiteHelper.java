package com.example.lokit.shush;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by lokit on 10-Jan-16.
 */

public class MySQLiteHelper extends SQLiteOpenHelper {

    public static final String TABLE_RECORDS = "records1";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_SESSION = "session";
    public static final String COLUMN_ROUNDNO = "roundno";
    public static final String COLUMN_ARROWS = "arrows";
    public static final String COLUMN_ROUNDSUM = "roundsum";
    public static final String COLUMN_ROUNDAVE = "average";

    public static final String EVENT_NAME = "event_name";
    public static final String EVENT_TYPE = "event_type";
    public static final String START_TIME = "start_time";
    private static final String END_TIME = "end_time";
    private static final String MUTE = "mute" ;
    private static final String VIBRATE = "vibrate" ;
    private static final String LATITUDE = "latitude";
    private static final String LONGITUDE = "longitude";
    private static final String RADIUS = "radius";
    private static final String MON  = "MON";
    private static final String TUE = "TUE";
    private static final String WED = "WED";
    private static final String THU = "THU";
    private static final String FRI = "FRI";
    private static final String SAT = "SAT";
    private static final String SUN = "SUN";



    private static final String DATABASE_NAME = "records.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_RECORDS + "(" + EVENT_NAME
            + " string primary key, " + EVENT_TYPE
            + " string, " + START_TIME + " string, " + END_TIME
            + " string, " + MUTE + " integer, " + VIBRATE
            + " integer, " +  MON + " int, "+ TUE + " int, "+ WED + " int, "+ THU + " int, "+ FRI + " int, "+ SAT + " int, "+ SUN + " int, " +LATITUDE + " double, " + LONGITUDE + " double, " + RADIUS +" double)";


    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //database.execSQL("drop table records");
        database.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.w(MySQLiteHelper.class.getName(),
                "Upgrading database from version " + oldVersion + " to "
                        + newVersion + ", which will destroy all old data");
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
        onCreate(db);
    }


    public void addRecord(Record record) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(EVENT_NAME, record.getename()); // Contact Name
        values.put(EVENT_TYPE, record.getetype()); // Contact Phone Number
        values.put(START_TIME, record.getstime()); // Contact Phone Number
        values.put(END_TIME, record.getetime());
        values.put(MUTE, record.getmute());
        values.put(VIBRATE, record.getvibr());
        values.put(LATITUDE, record.getLocation()[0]);
        values.put(LONGITUDE, record.getLocation()[1]);
        values.put(RADIUS, record.getLocation()[2]);
        values.put(MON, record.getWeekdays()[0]);
        values.put(TUE, record.getWeekdays()[1]);
        values.put(WED, record.getWeekdays()[2]);
        values.put(THU, record.getWeekdays()[3]);
        values.put(FRI, record.getWeekdays()[4]);
        values.put(SAT, record.getWeekdays()[5]);
        values.put(SUN, record.getWeekdays()[6]);

        // Inserting Row
        db.insert(TABLE_RECORDS, null, values);
        Log.e("Debug","Reached here");
    }

    public void removeRecord(Record r){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_RECORDS, EVENT_NAME + "=?", new String[] { r.getename()});
    }

    public ArrayList<Record> getAllRecords() {
        ArrayList<Record> recordList = new ArrayList<Record>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + TABLE_RECORDS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if(cursor!=null && cursor.getCount()>0) {
            Log.d("ADebugTag", "Value: " + Float.toString(cursor.getCount()));
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Record record = new Record();
                    record.setename(cursor.getString(0));
                    record.setetype(cursor.getString(1));
                    record.setstime(cursor.getString(2));
                    record.setetime(cursor.getString(3));
                    record.setmute(cursor.getInt(4));
                    record.setvibr(cursor.getInt(5));
                    ArrayList<Integer> week = new ArrayList<Integer>();
                    week.add(cursor.getInt(6));
                    week.add(cursor.getInt(7));
                    week.add(cursor.getInt(8));
                    week.add(cursor.getInt(9));
                    week.add(cursor.getInt(10));
                    week.add(cursor.getInt(11));
                    week.add(cursor.getInt(12));
                    record.setWeekdays(week);

                    ArrayList<Double> temp = new ArrayList<Double>();
                    temp.add(cursor.getDouble(13));
                    temp.add(cursor.getDouble(14));
                    temp.add(cursor.getDouble(15));
                    record.setLocation(temp);

                    recordList.add(record);
                } while (cursor.moveToNext());
            }
        }
        cursor.close();

        return recordList;
    }


    public int getLastSession() {
        SQLiteDatabase db = this.getReadableDatabase();
        String countQuery = "SELECT  * FROM " + TABLE_RECORDS;
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null) {
            cursor.moveToLast();
            return cursor.getInt(1);
        } else {
            return 0;
        }
    }
}
