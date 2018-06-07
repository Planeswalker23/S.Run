package com.nanbei.srun;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import entity.LocalUser;

/**
 * Created by tzc on 2017/11/3.
 */

public class LocalUserManager {
    private SQLiteOpenHelper sqLiteOpenHelper;
    public LocalUserManager(SQLiteOpenHelper sqLiteOpenHelper) {
        this.sqLiteOpenHelper = sqLiteOpenHelper;
    }
    public LocalUser insert(LocalUser localUser) {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("school", localUser.getSchool());
        values.put("id", localUser.getId());
        values.put("password", localUser.getPassword());
        database.insert("localuser", null, values);
        System.out.println("333::"+localUser.getId());
//        long id = database.insert("localuser", null, values);
//        localUser.setId((int) id);
        database.close();
        return localUser;
    }
//    public void delete(LocalUser localUser) {
//        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
//        database.delete("localuser", "uid=?", new String[]{"" + localUser.getUid()});
//        database.close();
//    }
    public void deleteAll() {
        SQLiteDatabase database = sqLiteOpenHelper.getWritableDatabase();
        database.execSQL("delete from localuser");
        database.close();
    }
    public LocalUser queryAll() {
        SQLiteDatabase database = sqLiteOpenHelper.getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from localuser"
                , new String[]{});
        LocalUser localUser = new LocalUser();
        localUser.setSchool("#");
        localUser.setId("#");
        localUser.setPassword("");

        while (cursor.moveToNext()) {
            localUser.setId(cursor.getString(0));
            localUser.setPassword(cursor.getString(1));
            localUser.setSchool(cursor.getString(2));
            System.out.println("local_uid::"+cursor.getString(0)+" "+cursor.getString(2));
        }
        cursor.close();
        return localUser;
    }

}
