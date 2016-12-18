package com.example.flaforgue.geophone.managers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Baptite_Portable on 18/12/2016.
 */

public class SQLManager extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "LOCATIONS";
    private static final int VERSION = 1;

    private static final String KEY_ID = "id";
    private static final String COLUMN_NUMBER = "number";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_LATITUDE = "latitude";

    private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + COLUMN_NUMBER + " INTEGER, " + COLUMN_LONGITUDE + " FLOAT," + COLUMN_LATITUDE + " FLOAT)";

    public SQLManager(Context context) {
        super(context, TABLE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        super.onDowngrade(db,oldVersion,newVersion);
    }

    public void fillTable(HashMap<String,List<Location>> m) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        // insert row
        for(String s : m.keySet()) {
            for (Location l : m.get(s)) {
                values.put(COLUMN_NUMBER, s);
                values.put(COLUMN_LONGITUDE, l.getLongitude());
                values.put(COLUMN_LATITUDE, l.getLatitude());
                db.insert(TABLE_NAME, null, values);
            }
        }

    }

    public HashMap<String,List<Location>> getData() {
        HashMap<String,List<Location>> map = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cNumber = db.rawQuery("SELECT DISTINCT" + COLUMN_NUMBER + " FROM " + TABLE_NAME, null);
        if(cNumber.moveToFirst()){
            List<Location> locations = new ArrayList<>();
            do{
                String number = cNumber.getString(0);
                Cursor cLocation = db.rawQuery("SELECT " + COLUMN_LONGITUDE + ", " + COLUMN_LATITUDE + " FROM " + TABLE_NAME + " WHERE " + COLUMN_NUMBER + "='" + number + "'", null);
                if (cLocation.moveToFirst()) {
                    do {
                        Double longitude = cLocation.getDouble(0);
                        Double latitude = cLocation.getDouble(1);
                        Location loc = new Location("Loaded location");
                        loc.setLongitude(longitude);
                        loc.setLatitude(latitude);
                        locations.add(loc);
                    } while(cLocation.moveToNext());
                }
                map.put(number,locations);
            }while(cNumber.moveToNext());
        }
        cNumber.close();
        db.close();
        return map;
    }
}
