package com.example.slack;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHandler extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHandler";
    private static final String DATABASE_NAME = "message.db";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " +
                DBContract.DBEntry.TABLE_NAME + " (" +
                DBContract.DBEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DBContract.DBEntry.COL_1 + " TEXT NOT NULL, " +
                DBContract.DBEntry.COL_2 + " TEXT NOT NULL" +
                ");";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(" DROP TABLE IF EXISTS "+ DBContract.DBEntry.TABLE_NAME);
        onCreate(db);
    }

    public Cursor getData(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = " SELECT * FROM " + DBContract.DBEntry.TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

}
