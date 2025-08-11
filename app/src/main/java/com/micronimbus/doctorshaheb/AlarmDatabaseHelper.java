package com.micronimbus.doctorshaheb;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class AlarmDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "medicine_alarms.db";
    private static final int DB_VERSION = 1;

    private static final String TABLE_NAME = "alarms";
    private static final String COL_ID = "id";
    private static final String COL_MED_NAME = "medicineName";
    private static final String COL_HOUR = "hour";
    private static final String COL_MINUTE = "minute";

    public AlarmDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MED_NAME + " TEXT, " +
                COL_HOUR + " INTEGER, " +
                COL_MINUTE + " INTEGER)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // For simplicity drop and recreate
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long addAlarm(MedicineAlarm alarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MED_NAME, alarm.medicineName);
        cv.put(COL_HOUR, alarm.hour);
        cv.put(COL_MINUTE, alarm.minute);
        return db.insert(TABLE_NAME, null, cv);
    }

    public List<MedicineAlarm> getAllAlarms() {
        List<MedicineAlarm> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(COL_ID));
                String medName = cursor.getString(cursor.getColumnIndexOrThrow(COL_MED_NAME));
                int hour = cursor.getInt(cursor.getColumnIndexOrThrow(COL_HOUR));
                int minute = cursor.getInt(cursor.getColumnIndexOrThrow(COL_MINUTE));
                list.add(new MedicineAlarm(id, medName, hour, minute));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }

    public int updateAlarm(MedicineAlarm alarm) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(COL_MED_NAME, alarm.medicineName);
        cv.put(COL_HOUR, alarm.hour);
        cv.put(COL_MINUTE, alarm.minute);
        return db.update(TABLE_NAME, cv, COL_ID + "=?", new String[]{String.valueOf(alarm.id)});
    }

    public void deleteAlarm(int id) {
        SQLiteDatabase db = getWritableDatabase();
        db.delete(TABLE_NAME, COL_ID + "=?", new String[]{String.valueOf(id)});
    }
}
