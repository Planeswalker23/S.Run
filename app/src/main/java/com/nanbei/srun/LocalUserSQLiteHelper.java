package com.nanbei.srun;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;


public class LocalUserSQLiteHelper extends SQLiteOpenHelper {
    public LocalUserSQLiteHelper(Context context) {
        super(context, "sports_localuser.db", null, 1);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table if not exists localuser" +
                "(id varchar(20) primary key," +
                "username varchar(20)," +
                "password varchar(20)," +
                "school varchar(30))");
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
