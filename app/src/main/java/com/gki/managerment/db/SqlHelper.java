package com.gki.managerment.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class SqlHelper extends SQLiteOpenHelper {
    /*
     * 创建语句
     */
    public SqlHelper(Context context, CursorFactory factory, int version) {
        super(context, "PAD", factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table LoginUser(id integer primary key autoincrement, userId varchar(50), userPswd varchar(50), loginTime varchar(19))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        // TODO Auto-generated method stub
    }
}
