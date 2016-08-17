package com.example.lokit.shush;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

public class MyApplication extends Application {
    public static SQLiteDatabase mDB = null;

    @Override
    public void onCreate()
    {
        super.onCreate();
        MySQLiteHelper SQLiteHelper = new MySQLiteHelper( this );
        mDB = SQLiteHelper.getWritableDatabase();

        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }

    public static SQLiteDatabase getDB() {
        return mDB;
    }


    protected void initSingletons()
    {
        // Initialize the instance of MySingleton
        //MySingleton.initInstance();
    }

    public void customAppMethod()
    {
        // Custom application method
    }

}
