package com.example.flaforgue.geophone.managers.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.location.Location;
import android.widget.Toast;

import com.example.flaforgue.geophone.R;

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
            + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + COLUMN_NUMBER + " VARCHAR(12), " + COLUMN_LONGITUDE + " FLOAT," + COLUMN_LATITUDE + " FLOAT)";

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

    /**
     * Récupération des données stockées dans la base de données interne
     * @return Retourne une HashMap avec la liste des numéros, mappée avec la liste des résultats des recherches
     */
    public HashMap<String,List<Location>> getData() {
        HashMap<String,List<Location>> map = new HashMap<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cNumber = db.rawQuery("SELECT " + COLUMN_NUMBER + " FROM " + TABLE_NAME, null);
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

    /**
     * Insertion du résultat d'une recherche
     * @param number Le numéro recherché
     * @param loc Le résultat de la recherche
     */
    public void insertLocation(String number, Location loc) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + TABLE_NAME + " ("
                + COLUMN_NUMBER + ", " + COLUMN_LONGITUDE + ", " + COLUMN_LATITUDE + ") Values ('"
                + number + "', '" + loc.getLongitude() + "', '" + loc.getLatitude() + "')";
        db.execSQL(query);

    }
}
