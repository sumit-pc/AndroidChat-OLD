package com.example.aymen.androidchat;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "objects.db";
    public static final String TABLE_NAME = "messages_list";
    public static final String COL1 = "ID";
    public static final String COL2 = "NAME";
    public static final String COL3 = "MESSAGE";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "("+COL1+" INTEGER PRIMARY KEY AUTOINCREMENT, "+COL2+" TEXT, "+COL3+" TEXT)";
        db.execSQL(createTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public boolean addData(String name, String message)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL2, name);
        contentValues.put(COL3, message);

        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result == -1)
        {
            return false;
        }
        else
        {
            return true;
        }
    }

    public Cursor getListMessage()
    {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor data = db.rawQuery("SELECT * FROM "+TABLE_NAME,null);
        return data;
    }
}
