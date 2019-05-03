package com.example.challenge1__andrescabrera;

import android.content.ContentValues;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class CamDatabaseHelper extends SQLiteOpenHelper{

    CamDatabaseHelper(Context context) {
        super(context, "camDB", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String tableForDB = "CREATE TABLE pictures (id integer PRIMARY KEY AUTOINCREMENT, "
                + "image blob NOT NULL)" ;

        db.execSQL(tableForDB);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS pictures");
        onCreate(db);
    }

    public void insertImageToDB(byte[] image, int n) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues pictureValues = new ContentValues();
        pictureValues.put("image", image);
        db.insert("pictures", null, pictureValues);
    }
}