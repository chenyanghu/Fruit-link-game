package com.huchenyang.lianliankan;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Hu on 2016/5/11.
 */
public class MyDBHelper extends SQLiteOpenHelper{
    private static final String DB_NAME = "gamedata.db"; //数据库名称
    private static final int version = 1; //数据库版本

    public MyDBHelper(Context context) {
        super(context, DB_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String usr ="create table users( name varchar(30) primary key,password varchar(30),topscore int)";
        String set = "create table setting(settingtype varchar(20) not null, state varchar(5) not null);";
        String iniusr="insert into users(name,password,topscore) values('admin','admin','20')";
        String iniusr2="insert into users(name,password,topscore) values('hu','hu','10')";
        String iniusr3="insert into users(name,password,topscore) values('li','li','30')";
        String iniset = "insert into setting(settingtype,state) values ('bgmusic','off');";
        String iniset2 = "insert into setting(settingtype,state) values ('audio','off');";
        db.execSQL(usr);
        db.execSQL(iniusr);
        db.execSQL(iniusr2);
        db.execSQL(iniusr3);
        db.execSQL(set);
        db.execSQL(iniset);
        db.execSQL(iniset2);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

    }

}
