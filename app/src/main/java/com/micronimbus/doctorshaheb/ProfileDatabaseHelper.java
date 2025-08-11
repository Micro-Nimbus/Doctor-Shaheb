package com.micronimbus.doctorshaheb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;

public class ProfileDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "UserProfile.db";
    private static final int DATABASE_VERSION = 1;

    private static final String TABLE_NAME = "profile";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_IMAGE_URI = "imageUri";
    private static final String COLUMN_ADDRESS = "address";
    private static final String COLUMN_AGE = "age";
    private static final String COLUMN_HEIGHT = "height";
    private static final String COLUMN_WEIGHT = "weight";
    private static final String COLUMN_BLOOD = "blood";

    public ProfileDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void updateImageUri(String newUri) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("imageUri", newUri);
        db.update(TABLE_NAME, values, "id = 1", null);
        db.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
                COLUMN_ID + " INTEGER PRIMARY KEY," +
                COLUMN_IMAGE_URI + " TEXT," +
                COLUMN_ADDRESS + " TEXT," +
                COLUMN_AGE + " TEXT," +
                COLUMN_HEIGHT + " TEXT," +
                COLUMN_WEIGHT + " TEXT," +
                COLUMN_BLOOD + " TEXT)";
        db.execSQL(CREATE_TABLE);

        // Insert an empty row (only one profile)
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, 1);
        db.insert(TABLE_NAME, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void saveProfile(String imageUri, String address, String age, String height, String weight, String blood) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_IMAGE_URI, imageUri);
        values.put(COLUMN_ADDRESS, address);
        values.put(COLUMN_AGE, age);
        values.put(COLUMN_HEIGHT, height);
        values.put(COLUMN_WEIGHT, weight);
        values.put(COLUMN_BLOOD, blood);

        db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{"1"});
        db.close();
    }

    public Cursor getProfile() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + "=1", null);
    }
}
